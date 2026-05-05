package org.example.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.domain.encapsulate.ResponseResult;
import org.example.domain.hotel.Hotel;
import org.example.feign.CommentStatsFeignClient;
import org.example.mapper.HotelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class HotelRatingScheduler {

    @Autowired
    private HotelMapper hotelMapper;

    @Autowired
    private CommentStatsFeignClient commentStatsFeignClient;

    @Scheduled(initialDelay = 60_000, fixedDelay = 600_000)
    public void refreshHotelRatings() {
        List<Hotel> hotels = hotelMapper.selectList(new LambdaQueryWrapper<Hotel>()
                .eq(Hotel::getIsDelete, 0));
        if (hotels == null || hotels.isEmpty()) {
            return;
        }

        Map<Long, BigDecimal> avgMap = new HashMap<>();
        try {
            ResponseResult<Map<String, Object>> res = commentStatsFeignClient.hotelAvgRatings(null);
            if (res != null && res.getCode() != null && res.getCode().equals(200) && res.getData() != null) {
                Object listObj = res.getData().get("list");
                if (listObj instanceof List<?> list) {
                    for (Object item : list) {
                        if (!(item instanceof Map<?, ?> m)) continue;
                        Object idObj = m.get("id");
                        Object ratingObj = m.get("rating");
                        if (idObj == null || ratingObj == null) continue;
                        try {
                            Long id = Long.valueOf(String.valueOf(idObj));
                            BigDecimal rating = new BigDecimal(String.valueOf(ratingObj)).setScale(1, RoundingMode.HALF_UP);
                            avgMap.put(id, rating);
                        } catch (Exception ignored) {
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }

        long now = System.currentTimeMillis();
        for (Hotel h : hotels) {
            if (h == null || h.getId() == null) continue;
            BigDecimal rating = avgMap.getOrDefault(h.getId(), new BigDecimal("5.0"));
            Hotel upd = new Hotel();
            upd.setId(h.getId());
            upd.setRating(rating);
            upd.setUpdateTime(now);
            upd.setUpdateBy(0L);
            hotelMapper.updateById(upd);
        }
    }
}
