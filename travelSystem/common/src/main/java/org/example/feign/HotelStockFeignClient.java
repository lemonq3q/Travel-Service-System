package org.example.feign;

import org.example.domain.encapsulate.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Map;

@FeignClient(value = "service-hotel")
public interface HotelStockFeignClient {

    @GetMapping("/hotel/stock/remain")
    ResponseResult<Map<String, Object>> getRemain(
            @RequestParam("roomId") Long roomId,
            @RequestParam("checkInDate") LocalDate checkInDate,
            @RequestParam("checkOutDate") LocalDate checkOutDate
    );

    @PostMapping("/hotel/stock/reserve")
    ResponseResult<Map<String, Object>> reserve(@RequestBody Map<String, Object> body);

    @PostMapping("/hotel/stock/release")
    ResponseResult<Map<String, Object>> release(@RequestBody Map<String, Object> body);
}
