package org.example.feign;

import org.example.domain.encapsulate.ResponseResult;
import org.example.domain.user.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "service-user")
public interface UserFeignClient {

    @GetMapping("/user/email")
    public ResponseResult<User> selectByEmail(@RequestParam("email") String email);

    @PostMapping("/user/register")
    public ResponseResult<User> register(@RequestBody User user);

    @PutMapping("/user/update/password")
    public ResponseResult updatePassword(@RequestBody User user);

}
