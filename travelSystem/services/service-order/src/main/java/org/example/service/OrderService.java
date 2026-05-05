package org.example.service;

import org.example.domain.encapsulate.ResponseResult;

import java.util.Map;

public interface OrderService {

    ResponseResult<Map<String, Object>> checkHotelRoomRemain(Map<String, Object> params);

    ResponseResult<Map<String, Object>> createHotelOrder(Map<String, Object> body);

    ResponseResult<Map<String, Object>> createScenicOrder(Map<String, Object> body);

    ResponseResult<Map<String, Object>> listMyHotelOrders(Map<String, Object> params);

    ResponseResult<Map<String, Object>> listMyScenicOrders(Map<String, Object> params);

    ResponseResult<Map<String, Object>> cancelHotelOrder(Map<String, Object> body);

    ResponseResult<Map<String, Object>> cancelScenicOrder(Map<String, Object> body);

    ResponseResult<Map<String, Object>> payHotelOrder(Map<String, Object> body);

    ResponseResult<Map<String, Object>> payScenicOrder(Map<String, Object> body);

    ResponseResult<Map<String, Object>> createTrainOrder(Map<String, Object> body);

    ResponseResult<Map<String, Object>> listMyTrainOrders(Map<String, Object> params);

    ResponseResult<Map<String, Object>> cancelTrainOrder(Map<String, Object> body);

    ResponseResult<Map<String, Object>> payTrainOrder(Map<String, Object> body);

    ResponseResult<Map<String, Object>> createAircraftOrder(Map<String, Object> body);

    ResponseResult<Map<String, Object>> listMyAircraftOrders(Map<String, Object> params);

    ResponseResult<Map<String, Object>> cancelAircraftOrder(Map<String, Object> body);

    ResponseResult<Map<String, Object>> payAircraftOrder(Map<String, Object> body);
}
