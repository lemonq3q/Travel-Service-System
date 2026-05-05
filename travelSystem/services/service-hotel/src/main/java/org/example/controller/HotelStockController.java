package org.example.controller;

import org.example.domain.encapsulate.ResponseResult;
import org.example.service.HotelStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/hotel/stock")
public class HotelStockController {

    @Autowired
    private HotelStockService hotelStockService;

    @GetMapping("/remain")
    public ResponseResult<Map<String, Object>> getRemain(
            @RequestParam("roomId") Long roomId,
            @RequestParam("checkInDate") LocalDate checkInDate,
            @RequestParam("checkOutDate") LocalDate checkOutDate
    ) {
        return hotelStockService.getRemain(roomId, checkInDate, checkOutDate);
    }

    @PostMapping("/reserve")
    public ResponseResult<Map<String, Object>> reserve(@RequestBody Map<String, Object> body) {
        Long roomId = body == null || body.get("roomId") == null ? null : Long.valueOf(String.valueOf(body.get("roomId")));
        LocalDate checkInDate = body == null || body.get("checkInDate") == null ? null : LocalDate.parse(String.valueOf(body.get("checkInDate")));
        LocalDate checkOutDate = body == null || body.get("checkOutDate") == null ? null : LocalDate.parse(String.valueOf(body.get("checkOutDate")));
        Integer roomNum = body == null || body.get("roomNum") == null ? null : Integer.valueOf(String.valueOf(body.get("roomNum")));
        return hotelStockService.reserve(roomId, checkInDate, checkOutDate, roomNum);
    }

    @PostMapping("/release")
    public ResponseResult<Map<String, Object>> release(@RequestBody Map<String, Object> body) {
        Long roomId = body == null || body.get("roomId") == null ? null : Long.valueOf(String.valueOf(body.get("roomId")));
        LocalDate checkInDate = body == null || body.get("checkInDate") == null ? null : LocalDate.parse(String.valueOf(body.get("checkInDate")));
        LocalDate checkOutDate = body == null || body.get("checkOutDate") == null ? null : LocalDate.parse(String.valueOf(body.get("checkOutDate")));
        Integer roomNum = body == null || body.get("roomNum") == null ? null : Integer.valueOf(String.valueOf(body.get("roomNum")));
        return hotelStockService.release(roomId, checkInDate, checkOutDate, roomNum);
    }
}
