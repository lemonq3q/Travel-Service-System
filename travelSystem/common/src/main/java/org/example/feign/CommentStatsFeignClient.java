package org.example.feign;

import org.example.domain.encapsulate.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(value = "service-comment")
public interface CommentStatsFeignClient {

    @GetMapping("/comment/hotel/avg")
    ResponseResult<Map<String, Object>> hotelAvgRatings(@RequestParam(value = "ids", required = false) String ids);

    @GetMapping("/comment/scenic/avg")
    ResponseResult<Map<String, Object>> scenicAvgRatings(@RequestParam(value = "ids", required = false) String ids);
}
