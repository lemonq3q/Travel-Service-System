package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.domain.encapsulate.ResponseResult;
import org.example.domain.hotel.HotelRoom;
import org.example.domain.hotel.RoomStock;
import org.example.mapper.HotelRoomMapper;
import org.example.mapper.RoomStockMapper;
import org.example.service.HotelStockService;
import org.example.utils.SystemCommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class HotelStockServiceImpl implements HotelStockService {

    private static final String LOCK_PREFIX = "lock:hotel:roomStock:";

    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT = new DefaultRedisScript<>(
            "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end",
            Long.class
    );

    @Autowired
    private HotelRoomMapper hotelRoomMapper;

    @Autowired
    private RoomStockMapper roomStockMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public ResponseResult<Map<String, Object>> getRemain(Long roomId, LocalDate checkInDate, LocalDate checkOutDate) {
        if (roomId == null || roomId <= 0) {
            return new ResponseResult<>(400, "roomId不能为空");
        }
        if (checkInDate == null || checkOutDate == null || !checkOutDate.isAfter(checkInDate)) {
            return new ResponseResult<>(400, "入住/离店日期不合法");
        }

        HotelRoom room = hotelRoomMapper.selectById(roomId);
        if (room == null || room.getIsDelete() != null && room.getIsDelete() == 1) {
            return new ResponseResult<>(404, "房型不存在");
        }
        int total = room.getTotalRoom() == null ? 0 : room.getTotalRoom();
        int remain = calcRemainRooms(roomId, total, checkInDate, checkOutDate);
        Map<String, Object> data = new HashMap<>();
        data.put("remainRooms", remain);
        return new ResponseResult<>(200, "查询成功", data);
    }

    @Override
    @Transactional
    public ResponseResult<Map<String, Object>> reserve(Long roomId, LocalDate checkInDate, LocalDate checkOutDate, Integer roomNum) {
        if (roomId == null || roomId <= 0) {
            return new ResponseResult<>(400, "roomId不能为空");
        }
        if (roomNum == null || roomNum <= 0) {
            return new ResponseResult<>(400, "roomNum不能为空");
        }
        if (checkInDate == null || checkOutDate == null || !checkOutDate.isAfter(checkInDate)) {
            return new ResponseResult<>(400, "入住/离店日期不合法");
        }

        String lockKey = buildLockKey(roomId, checkInDate, checkOutDate);
        String token = UUID.randomUUID().toString();
        if (!tryLock(lockKey, token, 15)) {
            return new ResponseResult<>(429, "库存繁忙，请重试");
        }
        try {
            HotelRoom room = hotelRoomMapper.selectById(roomId);
            if (room == null || room.getIsDelete() != null && room.getIsDelete() == 1) {
                return new ResponseResult<>(404, "房型不存在");
            }
            int total = room.getTotalRoom() == null ? 0 : room.getTotalRoom();
            int remain = calcRemainRooms(roomId, total, checkInDate, checkOutDate);
            if (remain < roomNum) {
                return new ResponseResult<>(400, "余房不足");
            }

            long now = System.currentTimeMillis();
            long by = SystemCommonUtil.getNowUserId() == null ? 0L : SystemCommonUtil.getNowUserId();
            for (LocalDate d = checkInDate; d.isBefore(checkOutDate); d = d.plusDays(1)) {
                RoomStock stock = roomStockMapper.selectOne(new LambdaQueryWrapper<RoomStock>()
                        .eq(RoomStock::getRoomId, roomId)
                        .eq(RoomStock::getStockDate, d)
                        .eq(RoomStock::getIsDelete, 0)
                        .last("limit 1"));
                if (stock == null) {
                    stock = new RoomStock();
                    stock.setRoomId(roomId);
                    stock.setStockDate(d);
                    stock.setOccupiedNum(roomNum);
                    stock.setCreateTime(now);
                    stock.setUpdateTime(now);
                    stock.setUpdateBy(by);
                    stock.setIsDelete(0);
                    roomStockMapper.insert(stock);
                } else {
                    Integer old = stock.getOccupiedNum() == null ? 0 : stock.getOccupiedNum();
                    stock.setOccupiedNum(old + roomNum);
                    stock.setUpdateTime(now);
                    stock.setUpdateBy(by);
                    roomStockMapper.updateById(stock);
                }
            }

            Map<String, Object> data = new HashMap<>();
            data.put("ok", true);
            return new ResponseResult<>(200, "预占成功", data);
        } finally {
            unlock(lockKey, token);
        }
    }

    @Override
    @Transactional
    public ResponseResult<Map<String, Object>> release(Long roomId, LocalDate checkInDate, LocalDate checkOutDate, Integer roomNum) {
        if (roomId == null || roomId <= 0) {
            return new ResponseResult<>(400, "roomId不能为空");
        }
        if (roomNum == null || roomNum <= 0) {
            return new ResponseResult<>(400, "roomNum不能为空");
        }
        if (checkInDate == null || checkOutDate == null || !checkOutDate.isAfter(checkInDate)) {
            return new ResponseResult<>(400, "入住/离店日期不合法");
        }

        String lockKey = buildLockKey(roomId, checkInDate, checkOutDate);
        String token = UUID.randomUUID().toString();
        if (!tryLock(lockKey, token, 15)) {
            return new ResponseResult<>(429, "库存繁忙，请重试");
        }
        try {
            long now = System.currentTimeMillis();
            long by = SystemCommonUtil.getNowUserId() == null ? 0L : SystemCommonUtil.getNowUserId();
            for (LocalDate d = checkInDate; d.isBefore(checkOutDate); d = d.plusDays(1)) {
                RoomStock stock = roomStockMapper.selectOne(new LambdaQueryWrapper<RoomStock>()
                        .eq(RoomStock::getRoomId, roomId)
                        .eq(RoomStock::getStockDate, d)
                        .eq(RoomStock::getIsDelete, 0)
                        .last("limit 1"));
                if (stock == null) {
                    continue;
                }
                int old = stock.getOccupiedNum() == null ? 0 : stock.getOccupiedNum();
                int next = old - roomNum;
                stock.setOccupiedNum(Math.max(0, next));
                stock.setUpdateTime(now);
                stock.setUpdateBy(by);
                roomStockMapper.updateById(stock);
            }
            Map<String, Object> data = new HashMap<>();
            data.put("ok", true);
            return new ResponseResult<>(200, "释放成功", data);
        } finally {
            unlock(lockKey, token);
        }
    }

    private int calcRemainRooms(Long roomId, int totalRoom, LocalDate checkInDate, LocalDate checkOutDate) {
        if (totalRoom <= 0) {
            return 0;
        }
        int remain = totalRoom;
        for (LocalDate d = checkInDate; d.isBefore(checkOutDate); d = d.plusDays(1)) {
            RoomStock stock = roomStockMapper.selectOne(new LambdaQueryWrapper<RoomStock>()
                    .eq(RoomStock::getRoomId, roomId)
                    .eq(RoomStock::getStockDate, d)
                    .eq(RoomStock::getIsDelete, 0)
                    .last("limit 1"));
            int occupied = stock == null || stock.getOccupiedNum() == null ? 0 : stock.getOccupiedNum();
            remain = Math.min(remain, Math.max(0, totalRoom - occupied));
            if (remain == 0) {
                return 0;
            }
        }
        return remain;
    }

    private String buildLockKey(Long roomId, LocalDate checkInDate, LocalDate checkOutDate) {
        return LOCK_PREFIX + roomId + ":" + checkInDate + ":" + checkOutDate;
    }

    private boolean tryLock(String key, String token, int seconds) {
        Boolean ok = stringRedisTemplate.opsForValue().setIfAbsent(key, token, seconds, TimeUnit.SECONDS);
        return ok != null && ok;
    }

    private void unlock(String key, String token) {
        try {
            stringRedisTemplate.execute(UNLOCK_SCRIPT, List.of(key), token);
        } catch (Exception ignored) {
        }
    }
}
