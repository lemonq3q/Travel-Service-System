package org.example.service;

import org.example.domain.encapsulate.ResponseResult;

import java.time.LocalDate;
import java.util.Map;

public interface HotelStockService {

    ResponseResult<Map<String, Object>> getRemain(Long roomId, LocalDate checkInDate, LocalDate checkOutDate);

    ResponseResult<Map<String, Object>> reserve(Long roomId, LocalDate checkInDate, LocalDate checkOutDate, Integer roomNum);

    ResponseResult<Map<String, Object>> release(Long roomId, LocalDate checkInDate, LocalDate checkOutDate, Integer roomNum);
}
