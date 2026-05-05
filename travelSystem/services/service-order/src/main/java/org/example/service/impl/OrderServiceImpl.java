package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.domain.encapsulate.ResponseResult;
import org.example.domain.order.AircraftOrder;
import org.example.domain.order.HotelOrder;
import org.example.domain.order.ScenicOrder;
import org.example.domain.order.TrainOrder;
import org.example.feign.HotelStockFeignClient;
import org.example.mapper.AircraftOrderMapper;
import org.example.mapper.HotelOrderMapper;
import org.example.mapper.ScenicOrderMapper;
import org.example.mapper.TrainOrderMapper;
import org.example.service.OrderService;
import org.example.utils.SystemCommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private HotelOrderMapper hotelOrderMapper;

    @Autowired
    private ScenicOrderMapper scenicOrderMapper;

    @Autowired
    private TrainOrderMapper trainOrderMapper;

    @Autowired
    private AircraftOrderMapper aircraftOrderMapper;

    @Autowired
    private HotelStockFeignClient hotelStockFeignClient;

    @Override
    public ResponseResult<Map<String, Object>> checkHotelRoomRemain(Map<String, Object> params) {
        Long roomId = asLong(firstNonNull(params, "as_room", "asRoom", "roomId"));
        String inStr = asString(firstNonNull(params, "check_in_date", "checkInDate"));
        String outStr = asString(firstNonNull(params, "check_out_date", "checkOutDate"));
        LocalDate checkInDate;
        LocalDate checkOutDate;
        try {
            checkInDate = inStr == null ? null : LocalDate.parse(inStr);
            checkOutDate = outStr == null ? null : LocalDate.parse(outStr);
        } catch (DateTimeParseException e) {
            return new ResponseResult<>(400, "日期格式错误");
        }
        if (roomId == null || roomId <= 0) {
            return new ResponseResult<>(400, "roomId不能为空");
        }
        if (checkInDate == null || checkOutDate == null || !checkOutDate.isAfter(checkInDate)) {
            return new ResponseResult<>(400, "入住/离店日期不合法");
        }

        ResponseResult<Map<String, Object>> res = hotelStockFeignClient.getRemain(roomId, checkInDate, checkOutDate);
        if (res == null || res.getCode() == null || !res.getCode().equals(200)) {
            return new ResponseResult<>(res == null ? 500 : res.getCode(), res == null ? "查询失败" : res.getMsg());
        }
        Integer remain = res.getData() == null ? 0 : asInt(res.getData().get("remainRooms"));
        Map<String, Object> data = new HashMap<>();
        data.put("remainRooms", remain == null ? 0 : remain);
        return new ResponseResult<>(200, "查询成功", data);
    }

    @Override
    @Transactional
    public ResponseResult<Map<String, Object>> createHotelOrder(Map<String, Object> body) {
        Long nowUser = safeNowUserId();
        Long asUser = nowUser != null && nowUser > 0 ? nowUser : asLong(firstNonNull(body, "as_user", "asUser"));
        Long asHotel = asLong(firstNonNull(body, "as_hotel", "asHotel"));
        Long asRoom = asLong(firstNonNull(body, "as_room", "asRoom"));
        Integer roomNum = asInt(firstNonNull(body, "room_num", "roomNum"));
        String inStr = asString(firstNonNull(body, "check_in_date", "checkInDate"));
        String outStr = asString(firstNonNull(body, "check_out_date", "checkOutDate"));
        String payAmountStr = asString(firstNonNull(body, "pay_amount", "payAmount"));

        LocalDate checkInDate;
        LocalDate checkOutDate;
        try {
            checkInDate = inStr == null ? null : LocalDate.parse(inStr);
            checkOutDate = outStr == null ? null : LocalDate.parse(outStr);
        } catch (DateTimeParseException e) {
            return new ResponseResult<>(400, "日期格式错误");
        }
        if (asUser == null || asUser <= 0) {
            return new ResponseResult<>(401, "未登录");
        }
        if (asHotel == null || asHotel <= 0 || asRoom == null || asRoom <= 0) {
            return new ResponseResult<>(400, "参数不完整");
        }
        if (roomNum == null || roomNum <= 0) {
            return new ResponseResult<>(400, "房间数量不合法");
        }
        if (checkInDate == null || checkOutDate == null || !checkOutDate.isAfter(checkInDate)) {
            return new ResponseResult<>(400, "入住/离店日期不合法");
        }

        BigDecimal payAmount;
        try {
            payAmount = payAmountStr == null ? BigDecimal.ZERO : new BigDecimal(payAmountStr);
        } catch (Exception e) {
            return new ResponseResult<>(400, "金额不合法");
        }

        Map<String, Object> reserveBody = new HashMap<>();
        reserveBody.put("roomId", asRoom);
        reserveBody.put("checkInDate", checkInDate.toString());
        reserveBody.put("checkOutDate", checkOutDate.toString());
        reserveBody.put("roomNum", roomNum);
        ResponseResult<Map<String, Object>> reserveRes = hotelStockFeignClient.reserve(reserveBody);
        if (reserveRes == null || reserveRes.getCode() == null || !reserveRes.getCode().equals(200)) {
            return new ResponseResult<>(reserveRes == null ? 500 : reserveRes.getCode(), reserveRes == null ? "余房不足" : reserveRes.getMsg());
        }

        long now = System.currentTimeMillis();
        HotelOrder order = new HotelOrder();
        order.setAsUser(asUser);
        order.setAsHotel(asHotel);
        order.setAsRoom(asRoom);
        order.setRoomNum(roomNum);
        order.setCheckInDate(checkInDate);
        order.setCheckOutDate(checkOutDate);
        order.setPayAmount(payAmount);
        order.setIsPay(0);
        order.setCreateTime(now);
        order.setUpdateTime(now);
        order.setUpdateBy(asUser);
        order.setIsDelete(0);

        try {
            hotelOrderMapper.insert(order);
        } catch (Exception e) {
            Map<String, Object> releaseBody = new HashMap<>();
            releaseBody.put("roomId", asRoom);
            releaseBody.put("checkInDate", checkInDate.toString());
            releaseBody.put("checkOutDate", checkOutDate.toString());
            releaseBody.put("roomNum", roomNum);
            try {
                hotelStockFeignClient.release(releaseBody);
            } catch (Exception ignored) {
            }
            return new ResponseResult<>(500, "创建订单失败");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", order.getId());
        return new ResponseResult<>(200, "ok", data);
    }

    @Override
    @Transactional
    public ResponseResult<Map<String, Object>> createScenicOrder(Map<String, Object> body) {
        Long nowUser = safeNowUserId();
        Long asUser = nowUser != null && nowUser > 0 ? nowUser : asLong(firstNonNull(body, "as_user", "asUser"));
        Long asScenic = asLong(firstNonNull(body, "as_scenic", "asScenic"));
        String payAmountStr = asString(firstNonNull(body, "pay_amount", "payAmount"));

        if (asUser == null || asUser <= 0) {
            return new ResponseResult<>(401, "未登录");
        }
        if (asScenic == null || asScenic <= 0) {
            return new ResponseResult<>(400, "参数不完整");
        }

        BigDecimal payAmount;
        try {
            payAmount = payAmountStr == null ? BigDecimal.ZERO : new BigDecimal(payAmountStr);
        } catch (Exception e) {
            return new ResponseResult<>(400, "金额不合法");
        }

        long now = System.currentTimeMillis();
        ScenicOrder order = new ScenicOrder();
        order.setAsUser(asUser);
        order.setAsScenic(asScenic);
        order.setPayAmount(payAmount);
        order.setIsPay(0);
        order.setCreateTime(now);
        order.setUpdateTime(now);
        order.setUpdateBy(asUser);
        order.setIsDelete(0);
        scenicOrderMapper.insert(order);

        Map<String, Object> data = new HashMap<>();
        data.put("id", order.getId());
        return new ResponseResult<>(200, "ok", data);
    }

    @Override
    public ResponseResult<Map<String, Object>> listMyHotelOrders(Map<String, Object> params) {
        Long nowUser = safeNowUserId();
        Long asUser = nowUser != null && nowUser > 0 ? nowUser : asLong(firstNonNull(params, "as_user", "asUser"));
        if (asUser == null || asUser <= 0) {
            return new ResponseResult<>(401, "未登录");
        }

        List<HotelOrder> list = hotelOrderMapper.selectList(new LambdaQueryWrapper<HotelOrder>()
                .eq(HotelOrder::getAsUser, asUser)
                .orderByDesc(HotelOrder::getId));

        List<Map<String, Object>> out = new ArrayList<>();
        if (list != null) {
            for (HotelOrder o : list) {
                if (o == null) continue;
                out.add(toHotelOrderView(o));
            }
        }
        Map<String, Object> data = new HashMap<>();
        data.put("list", out);
        return new ResponseResult<>(200, "ok", data);
    }

    @Override
    public ResponseResult<Map<String, Object>> listMyScenicOrders(Map<String, Object> params) {
        Long nowUser = safeNowUserId();
        Long asUser = nowUser != null && nowUser > 0 ? nowUser : asLong(firstNonNull(params, "as_user", "asUser"));
        if (asUser == null || asUser <= 0) {
            return new ResponseResult<>(401, "未登录");
        }

        List<ScenicOrder> list = scenicOrderMapper.selectList(new LambdaQueryWrapper<ScenicOrder>()
                .eq(ScenicOrder::getAsUser, asUser)
                .orderByDesc(ScenicOrder::getId));

        List<Map<String, Object>> out = new ArrayList<>();
        if (list != null) {
            for (ScenicOrder o : list) {
                if (o == null) continue;
                out.add(toScenicOrderView(o));
            }
        }
        Map<String, Object> data = new HashMap<>();
        data.put("list", out);
        return new ResponseResult<>(200, "ok", data);
    }

    @Override
    @Transactional
    public ResponseResult<Map<String, Object>> cancelHotelOrder(Map<String, Object> body) {
        Long nowUser = safeNowUserId();
        Long asUser = nowUser != null && nowUser > 0 ? nowUser : asLong(firstNonNull(body, "as_user", "asUser"));
        Long id = asLong(firstNonNull(body, "id", "orderId"));
        if (asUser == null || asUser <= 0) {
            return new ResponseResult<>(401, "未登录");
        }
        if (id == null || id <= 0) {
            return new ResponseResult<>(400, "id不能为空");
        }

        HotelOrder order = hotelOrderMapper.selectById(id);
        if (order == null || order.getIsDelete() != null && order.getIsDelete() == 1) {
            return new ResponseResult<>(404, "订单不存在");
        }
        if (!Objects.equals(order.getAsUser(), asUser)) {
            return new ResponseResult<>(403, "无权限");
        }
        if (order.getIsPay() != null && order.getIsPay() == 1) {
            return new ResponseResult<>(400, "已支付订单不可取消");
        }

        Map<String, Object> releaseBody = new HashMap<>();
        releaseBody.put("roomId", order.getAsRoom());
        releaseBody.put("checkInDate", order.getCheckInDate() == null ? null : order.getCheckInDate().toString());
        releaseBody.put("checkOutDate", order.getCheckOutDate() == null ? null : order.getCheckOutDate().toString());
        releaseBody.put("roomNum", order.getRoomNum());
        ResponseResult<Map<String, Object>> releaseRes = hotelStockFeignClient.release(releaseBody);
        if (releaseRes == null || releaseRes.getCode() == null || !releaseRes.getCode().equals(200)) {
            return new ResponseResult<>(releaseRes == null ? 500 : releaseRes.getCode(), releaseRes == null ? "取消失败" : releaseRes.getMsg());
        }

        long now = System.currentTimeMillis();
        order.setIsDelete(1);
        order.setUpdateTime(now);
        order.setUpdateBy(asUser);
        hotelOrderMapper.updateById(order);

        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        return new ResponseResult<>(200, "ok", data);
    }

    @Override
    @Transactional
    public ResponseResult<Map<String, Object>> cancelScenicOrder(Map<String, Object> body) {
        Long nowUser = safeNowUserId();
        Long asUser = nowUser != null && nowUser > 0 ? nowUser : asLong(firstNonNull(body, "as_user", "asUser"));
        Long id = asLong(firstNonNull(body, "id", "orderId"));
        if (asUser == null || asUser <= 0) {
            return new ResponseResult<>(401, "未登录");
        }
        if (id == null || id <= 0) {
            return new ResponseResult<>(400, "id不能为空");
        }

        ScenicOrder order = scenicOrderMapper.selectById(id);
        if (order == null || order.getIsDelete() != null && order.getIsDelete() == 1) {
            return new ResponseResult<>(404, "订单不存在");
        }
        if (!Objects.equals(order.getAsUser(), asUser)) {
            return new ResponseResult<>(403, "无权限");
        }
        if (order.getIsPay() != null && order.getIsPay() == 1) {
            return new ResponseResult<>(400, "已支付订单不可取消");
        }

        long now = System.currentTimeMillis();
        order.setIsDelete(1);
        order.setUpdateTime(now);
        order.setUpdateBy(asUser);
        scenicOrderMapper.updateById(order);

        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        return new ResponseResult<>(200, "ok", data);
    }

    @Override
    @Transactional
    public ResponseResult<Map<String, Object>> payHotelOrder(Map<String, Object> body) {
        Long nowUser = safeNowUserId();
        Long asUser = nowUser != null && nowUser > 0 ? nowUser : asLong(firstNonNull(body, "as_user", "asUser"));
        Long id = asLong(firstNonNull(body, "id", "orderId"));
        if (asUser == null || asUser <= 0) {
            return new ResponseResult<>(401, "未登录");
        }
        if (id == null || id <= 0) {
            return new ResponseResult<>(400, "id不能为空");
        }

        HotelOrder order = hotelOrderMapper.selectById(id);
        if (order == null || order.getIsDelete() != null && order.getIsDelete() == 1) {
            return new ResponseResult<>(404, "订单不存在");
        }
        if (!Objects.equals(order.getAsUser(), asUser)) {
            return new ResponseResult<>(403, "无权限");
        }
        if (order.getIsPay() != null && order.getIsPay() == 1) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", id);
            return new ResponseResult<>(200, "ok", data);
        }

        long now = System.currentTimeMillis();
        order.setIsPay(1);
        order.setUpdateTime(now);
        order.setUpdateBy(asUser);
        hotelOrderMapper.updateById(order);

        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        return new ResponseResult<>(200, "ok", data);
    }

    @Override
    @Transactional
    public ResponseResult<Map<String, Object>> payScenicOrder(Map<String, Object> body) {
        Long nowUser = safeNowUserId();
        Long asUser = nowUser != null && nowUser > 0 ? nowUser : asLong(firstNonNull(body, "as_user", "asUser"));
        Long id = asLong(firstNonNull(body, "id", "orderId"));
        if (asUser == null || asUser <= 0) {
            return new ResponseResult<>(401, "未登录");
        }
        if (id == null || id <= 0) {
            return new ResponseResult<>(400, "id不能为空");
        }

        ScenicOrder order = scenicOrderMapper.selectById(id);
        if (order == null || order.getIsDelete() != null && order.getIsDelete() == 1) {
            return new ResponseResult<>(404, "订单不存在");
        }
        if (!Objects.equals(order.getAsUser(), asUser)) {
            return new ResponseResult<>(403, "无权限");
        }
        if (order.getIsPay() != null && order.getIsPay() == 1) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", id);
            return new ResponseResult<>(200, "ok", data);
        }

        long now = System.currentTimeMillis();
        order.setIsPay(1);
        order.setUpdateTime(now);
        order.setUpdateBy(asUser);
        scenicOrderMapper.updateById(order);

        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        return new ResponseResult<>(200, "ok", data);
    }

    @Override
    @Transactional
    public ResponseResult<Map<String, Object>> createTrainOrder(Map<String, Object> body) {
        Long nowUser = safeNowUserId();
        Long asUser = nowUser != null && nowUser > 0 ? nowUser : asLong(firstNonNull(body, "as_user", "asUser"));
        String asTrain = asString(firstNonNull(body, "as_train", "asTrain"));
        String startStationName = asString(firstNonNull(body, "start_station_name", "startStationName"));
        String endStationName = asString(firstNonNull(body, "end_station_name", "endStationName"));
        String typeName = asString(firstNonNull(body, "type_name", "typeName"));
        String payAmountStr = asString(firstNonNull(body, "pay_amount", "pay_amount", "payAmount"));
        if (asUser == null || asUser <= 0) {
            return new ResponseResult<>(401, "未登录");
        }
        if (asTrain == null || typeName == null || startStationName == null || endStationName == null) {
            return new ResponseResult<>(400, "参数不完整");
        }

        BigDecimal payAmount;
        try {
            payAmount = payAmountStr == null ? BigDecimal.ZERO : new BigDecimal(payAmountStr);
        } catch (Exception e) {
            return new ResponseResult<>(400, "金额不合法");
        }

        long now = System.currentTimeMillis();
        TrainOrder order = new TrainOrder();
        order.setAsUser(asUser);
        order.setAsTrain(asTrain);
        order.setStartStationName(startStationName);
        order.setEndStationName(endStationName);
        order.setTypeName(typeName);
        order.setPayAmount(payAmount);
        order.setIsPay(0);
        order.setCreateTime(now);
        order.setUpdateTime(now);
        order.setUpdateBy(asUser);
        order.setIsDelete(0);
        trainOrderMapper.insert(order);

        Map<String, Object> data = new HashMap<>();
        data.put("id", order.getId());
        return new ResponseResult<>(200, "ok", data);
    }

    @Override
    public ResponseResult<Map<String, Object>> listMyTrainOrders(Map<String, Object> params) {
        Long nowUser = safeNowUserId();
        Long asUser = nowUser != null && nowUser > 0 ? nowUser : asLong(firstNonNull(params, "as_user", "asUser"));
        if (asUser == null || asUser <= 0) {
            return new ResponseResult<>(401, "未登录");
        }
        List<TrainOrder> list = trainOrderMapper.selectList(new LambdaQueryWrapper<TrainOrder>()
                .eq(TrainOrder::getAsUser, asUser)
                .orderByDesc(TrainOrder::getId));

        List<Map<String, Object>> out = new ArrayList<>();
        if (list != null) {
            for (TrainOrder o : list) {
                if (o == null) continue;
                out.add(toTrainOrderView(o));
            }
        }
        Map<String, Object> data = new HashMap<>();
        data.put("list", out);
        return new ResponseResult<>(200, "ok", data);
    }

    @Override
    @Transactional
    public ResponseResult<Map<String, Object>> cancelTrainOrder(Map<String, Object> body) {
        Long nowUser = safeNowUserId();
        Long asUser = nowUser != null && nowUser > 0 ? nowUser : asLong(firstNonNull(body, "as_user", "asUser"));
        Long id = asLong(firstNonNull(body, "id", "orderId"));
        if (asUser == null || asUser <= 0) {
            return new ResponseResult<>(401, "未登录");
        }
        if (id == null || id <= 0) {
            return new ResponseResult<>(400, "id不能为空");
        }

        TrainOrder order = trainOrderMapper.selectById(id);
        if (order == null || order.getIsDelete() != null && order.getIsDelete() == 1) {
            return new ResponseResult<>(404, "订单不存在");
        }
        if (!Objects.equals(order.getAsUser(), asUser)) {
            return new ResponseResult<>(403, "无权限");
        }
        if (order.getIsPay() != null && order.getIsPay() == 1) {
            return new ResponseResult<>(400, "已支付订单不可取消");
        }

        long now = System.currentTimeMillis();
        order.setIsDelete(1);
        order.setUpdateTime(now);
        order.setUpdateBy(asUser);
        trainOrderMapper.updateById(order);

        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        return new ResponseResult<>(200, "ok", data);
    }

    @Override
    @Transactional
    public ResponseResult<Map<String, Object>> payTrainOrder(Map<String, Object> body) {
        Long nowUser = safeNowUserId();
        Long asUser = nowUser != null && nowUser > 0 ? nowUser : asLong(firstNonNull(body, "as_user", "asUser"));
        Long id = asLong(firstNonNull(body, "id", "orderId"));
        if (asUser == null || asUser <= 0) {
            return new ResponseResult<>(401, "未登录");
        }
        if (id == null || id <= 0) {
            return new ResponseResult<>(400, "id不能为空");
        }

        TrainOrder order = trainOrderMapper.selectById(id);
        if (order == null || order.getIsDelete() != null && order.getIsDelete() == 1) {
            return new ResponseResult<>(404, "订单不存在");
        }
        if (!Objects.equals(order.getAsUser(), asUser)) {
            return new ResponseResult<>(403, "无权限");
        }
        if (order.getIsPay() != null && order.getIsPay() == 1) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", id);
            return new ResponseResult<>(200, "ok", data);
        }

        long now = System.currentTimeMillis();
        order.setIsPay(1);
        order.setUpdateTime(now);
        order.setUpdateBy(asUser);
        trainOrderMapper.updateById(order);

        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        return new ResponseResult<>(200, "ok", data);
    }

    @Override
    @Transactional
    public ResponseResult<Map<String, Object>> createAircraftOrder(Map<String, Object> body) {
        Long nowUser = safeNowUserId();
        Long asUser = nowUser != null && nowUser > 0 ? nowUser : asLong(firstNonNull(body, "as_user", "asUser"));
        String asAircraft = asString(firstNonNull(body, "as_aircraft", "asAircraft"));
        String startStationName = asString(firstNonNull(body, "start_station_name", "startStationName"));
        String endStationName = asString(firstNonNull(body, "end_station_name", "endStationName"));
        String typeName = asString(firstNonNull(body, "type_name", "typeName"));
        String payAmountStr = asString(firstNonNull(body, "pay_amount", "pay_amount", "payAmount"));
        if (asUser == null || asUser <= 0) {
            return new ResponseResult<>(401, "未登录");
        }
        if (asAircraft == null || typeName == null || startStationName == null || endStationName == null) {
            return new ResponseResult<>(400, "参数不完整");
        }

        BigDecimal payAmount;
        try {
            payAmount = payAmountStr == null ? BigDecimal.ZERO : new BigDecimal(payAmountStr);
        } catch (Exception e) {
            return new ResponseResult<>(400, "金额不合法");
        }

        long now = System.currentTimeMillis();
        AircraftOrder order = new AircraftOrder();
        order.setAsUser(asUser);
        order.setAsAircraft(asAircraft);
        order.setStartStationName(startStationName);
        order.setEndStationName(endStationName);
        order.setTypeName(typeName);
        order.setPayAmount(payAmount);
        order.setIsPay(0);
        order.setCreateTime(now);
        order.setUpdateTime(now);
        order.setUpdateBy(asUser);
        order.setIsDelete(0);
        aircraftOrderMapper.insert(order);

        Map<String, Object> data = new HashMap<>();
        data.put("id", order.getId());
        return new ResponseResult<>(200, "ok", data);
    }

    @Override
    public ResponseResult<Map<String, Object>> listMyAircraftOrders(Map<String, Object> params) {
        Long nowUser = safeNowUserId();
        Long asUser = nowUser != null && nowUser > 0 ? nowUser : asLong(firstNonNull(params, "as_user", "asUser"));
        if (asUser == null || asUser <= 0) {
            return new ResponseResult<>(401, "未登录");
        }
        List<AircraftOrder> list = aircraftOrderMapper.selectList(new LambdaQueryWrapper<AircraftOrder>()
                .eq(AircraftOrder::getAsUser, asUser)
                .orderByDesc(AircraftOrder::getId));

        List<Map<String, Object>> out = new ArrayList<>();
        if (list != null) {
            for (AircraftOrder o : list) {
                if (o == null) continue;
                out.add(toAircraftOrderView(o));
            }
        }
        Map<String, Object> data = new HashMap<>();
        data.put("list", out);
        return new ResponseResult<>(200, "ok", data);
    }

    @Override
    @Transactional
    public ResponseResult<Map<String, Object>> cancelAircraftOrder(Map<String, Object> body) {
        Long nowUser = safeNowUserId();
        Long asUser = nowUser != null && nowUser > 0 ? nowUser : asLong(firstNonNull(body, "as_user", "asUser"));
        Long id = asLong(firstNonNull(body, "id", "orderId"));
        if (asUser == null || asUser <= 0) {
            return new ResponseResult<>(401, "未登录");
        }
        if (id == null || id <= 0) {
            return new ResponseResult<>(400, "id不能为空");
        }

        AircraftOrder order = aircraftOrderMapper.selectById(id);
        if (order == null || order.getIsDelete() != null && order.getIsDelete() == 1) {
            return new ResponseResult<>(404, "订单不存在");
        }
        if (!Objects.equals(order.getAsUser(), asUser)) {
            return new ResponseResult<>(403, "无权限");
        }
        if (order.getIsPay() != null && order.getIsPay() == 1) {
            return new ResponseResult<>(400, "已支付订单不可取消");
        }

        long now = System.currentTimeMillis();
        order.setIsDelete(1);
        order.setUpdateTime(now);
        order.setUpdateBy(asUser);
        aircraftOrderMapper.updateById(order);

        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        return new ResponseResult<>(200, "ok", data);
    }

    @Override
    @Transactional
    public ResponseResult<Map<String, Object>> payAircraftOrder(Map<String, Object> body) {
        Long nowUser = safeNowUserId();
        Long asUser = nowUser != null && nowUser > 0 ? nowUser : asLong(firstNonNull(body, "as_user", "asUser"));
        Long id = asLong(firstNonNull(body, "id", "orderId"));
        if (asUser == null || asUser <= 0) {
            return new ResponseResult<>(401, "未登录");
        }
        if (id == null || id <= 0) {
            return new ResponseResult<>(400, "id不能为空");
        }

        AircraftOrder order = aircraftOrderMapper.selectById(id);
        if (order == null || order.getIsDelete() != null && order.getIsDelete() == 1) {
            return new ResponseResult<>(404, "订单不存在");
        }
        if (!Objects.equals(order.getAsUser(), asUser)) {
            return new ResponseResult<>(403, "无权限");
        }
        if (order.getIsPay() != null && order.getIsPay() == 1) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", id);
            return new ResponseResult<>(200, "ok", data);
        }

        long now = System.currentTimeMillis();
        order.setIsPay(1);
        order.setUpdateTime(now);
        order.setUpdateBy(asUser);
        aircraftOrderMapper.updateById(order);

        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        return new ResponseResult<>(200, "ok", data);
    }

    private Map<String, Object> toHotelOrderView(HotelOrder o) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", o.getId());
        m.put("as_user", o.getAsUser());
        m.put("as_hotel", o.getAsHotel());
        m.put("as_room", o.getAsRoom());
        m.put("room_num", o.getRoomNum());
        m.put("check_in_date", o.getCheckInDate() == null ? null : o.getCheckInDate().toString());
        m.put("check_out_date", o.getCheckOutDate() == null ? null : o.getCheckOutDate().toString());
        m.put("pay_amount", o.getPayAmount() == null ? "0.00" : o.getPayAmount().toPlainString());
        m.put("is_pay", o.getIsPay() == null ? 0 : o.getIsPay());
        m.put("status", o.getIsDelete() != null && o.getIsDelete() == 1 ? "CANCELLED" : "NORMAL");
        m.put("created_at", formatIso(o.getCreateTime()));
        return m;
    }

    private Map<String, Object> toScenicOrderView(ScenicOrder o) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", o.getId());
        m.put("as_user", o.getAsUser());
        m.put("as_scenic", o.getAsScenic());
        m.put("pay_amount", o.getPayAmount() == null ? "0.00" : o.getPayAmount().toPlainString());
        m.put("is_pay", o.getIsPay() == null ? 0 : o.getIsPay());
        m.put("status", o.getIsDelete() != null && o.getIsDelete() == 1 ? "CANCELLED" : "NORMAL");
        m.put("created_at", formatIso(o.getCreateTime()));
        return m;
    }

    private Map<String, Object> toTrainOrderView(TrainOrder o) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", o.getId());
        m.put("as_user", o.getAsUser());
        m.put("as_train", o.getAsTrain());
        m.put("start_station_name", o.getStartStationName());
        m.put("end_station_name", o.getEndStationName());
        m.put("type_name", o.getTypeName());
        String pay = o.getPayAmount() == null ? "0.00" : o.getPayAmount().toPlainString();
        m.put("pay_amount", pay);
        m.put("pay_amount", pay);
        m.put("is_pay", o.getIsPay() == null ? 0 : o.getIsPay());
        m.put("status", o.getIsDelete() != null && o.getIsDelete() == 1 ? "CANCELLED" : "NORMAL");
        m.put("created_at", formatIso(o.getCreateTime()));
        return m;
    }

    private Map<String, Object> toAircraftOrderView(AircraftOrder o) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", o.getId());
        m.put("as_user", o.getAsUser());
        m.put("as_aircraft", o.getAsAircraft());
        m.put("start_station_name", o.getStartStationName());
        m.put("end_station_name", o.getEndStationName());
        m.put("type_name", o.getTypeName());
        String pay = o.getPayAmount() == null ? "0.00" : o.getPayAmount().toPlainString();
        m.put("pay_amount", pay);
        m.put("pay_amount", pay);
        m.put("is_pay", o.getIsPay() == null ? 0 : o.getIsPay());
        m.put("status", o.getIsDelete() != null && o.getIsDelete() == 1 ? "CANCELLED" : "NORMAL");
        m.put("created_at", formatIso(o.getCreateTime()));
        return m;
    }

    private String formatIso(Long millis) {
        if (millis == null || millis <= 0) return null;
        try {
            return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toInstant().toString();
        } catch (Exception e) {
            return null;
        }
    }

    private Long safeNowUserId() {
        try {
            Long id = SystemCommonUtil.getNowUserId();
            return id == null ? 0L : id;
        } catch (Exception e) {
            return 0L;
        }
    }

    private Object firstNonNull(Map<String, Object> map, String... keys) {
        if (map == null || keys == null) return null;
        for (String k : keys) {
            if (k == null) continue;
            Object v = map.get(k);
            if (v != null) return v;
        }
        return null;
    }

    private String asString(Object v) {
        if (v == null) return null;
        String s = String.valueOf(v);
        return s.isBlank() ? null : s;
    }

    private Long asLong(Object v) {
        if (v == null) return null;
        try {
            return Long.valueOf(String.valueOf(v));
        } catch (Exception e) {
            return null;
        }
    }

    private Integer asInt(Object v) {
        if (v == null) return null;
        try {
            return Integer.valueOf(String.valueOf(v));
        } catch (Exception e) {
            return null;
        }
    }
}
