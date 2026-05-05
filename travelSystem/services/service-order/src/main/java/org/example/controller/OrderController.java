package org.example.controller;

import org.example.domain.encapsulate.ResponseResult;
import org.example.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/hotel/remain")
    public ResponseResult<Map<String, Object>> checkHotelRoomRemain(@RequestParam Map<String, Object> params) {
        return orderService.checkHotelRoomRemain(params);
    }

    @PostMapping("/hotel/create")
    public ResponseResult<Map<String, Object>> createHotelOrder(@RequestBody Map<String, Object> body) {
        return orderService.createHotelOrder(body);
    }

    @GetMapping("/hotel/my")
    public ResponseResult<Map<String, Object>> listMyHotelOrders(@RequestParam(required = false) Map<String, Object> params) {
        return orderService.listMyHotelOrders(params == null ? new HashMap<>() : params);
    }

    @PostMapping("/hotel/cancel")
    public ResponseResult<Map<String, Object>> cancelHotelOrder(@RequestBody Map<String, Object> body) {
        return orderService.cancelHotelOrder(body);
    }

    @PostMapping("/hotel/pay")
    public ResponseResult<Map<String, Object>> payHotelOrder(@RequestBody Map<String, Object> body) {
        return orderService.payHotelOrder(body);
    }

    @PostMapping("/scenic/create")
    public ResponseResult<Map<String, Object>> createScenicOrder(@RequestBody Map<String, Object> body) {
        return orderService.createScenicOrder(body);
    }

    @GetMapping("/scenic/my")
    public ResponseResult<Map<String, Object>> listMyScenicOrders(@RequestParam(required = false) Map<String, Object> params) {
        return orderService.listMyScenicOrders(params == null ? new HashMap<>() : params);
    }

    @PostMapping("/scenic/cancel")
    public ResponseResult<Map<String, Object>> cancelScenicOrder(@RequestBody Map<String, Object> body) {
        return orderService.cancelScenicOrder(body);
    }

    @PostMapping("/scenic/pay")
    public ResponseResult<Map<String, Object>> payScenicOrder(@RequestBody Map<String, Object> body) {
        return orderService.payScenicOrder(body);
    }

    @PostMapping("/train/create")
    public ResponseResult<Map<String, Object>> createTrainOrder(@RequestBody Map<String, Object> body) {
        return orderService.createTrainOrder(body);
    }

    @GetMapping("/train/my")
    public ResponseResult<Map<String, Object>> listMyTrainOrders(@RequestParam(required = false) Map<String, Object> params) {
        return orderService.listMyTrainOrders(params == null ? new HashMap<>() : params);
    }

    @PostMapping("/train/cancel")
    public ResponseResult<Map<String, Object>> cancelTrainOrder(@RequestBody Map<String, Object> body) {
        return orderService.cancelTrainOrder(body);
    }

    @PostMapping("/train/pay")
    public ResponseResult<Map<String, Object>> payTrainOrder(@RequestBody Map<String, Object> body) {
        return orderService.payTrainOrder(body);
    }

    @PostMapping("/aircraft/create")
    public ResponseResult<Map<String, Object>> createAircraftOrder(@RequestBody Map<String, Object> body) {
        return orderService.createAircraftOrder(body);
    }

    @GetMapping("/aircraft/my")
    public ResponseResult<Map<String, Object>> listMyAircraftOrders(@RequestParam(required = false) Map<String, Object> params) {
        return orderService.listMyAircraftOrders(params == null ? new HashMap<>() : params);
    }

    @PostMapping("/aircraft/cancel")
    public ResponseResult<Map<String, Object>> cancelAircraftOrder(@RequestBody Map<String, Object> body) {
        return orderService.cancelAircraftOrder(body);
    }

    @PostMapping("/aircraft/pay")
    public ResponseResult<Map<String, Object>> payAircraftOrder(@RequestBody Map<String, Object> body) {
        return orderService.payAircraftOrder(body);
    }
}
