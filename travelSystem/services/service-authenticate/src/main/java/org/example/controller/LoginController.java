package org.example.controller;


import jakarta.websocket.server.PathParam;
import org.example.domain.encapsulate.ResponseResult;
import org.example.domain.user.User;
import org.example.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private LoginService loginService;


    @PostMapping("/login")
    public ResponseResult login(@RequestBody User user){
        return loginService.login(user);
    }


    @PostMapping("/register")
    public ResponseResult<User> register(@RequestBody User user){
        return loginService.registerPersonal(user);
    }

    @GetMapping("/logout")
    public ResponseResult logout(){
        return loginService.logout();
    }

    @GetMapping("/code")
    public ResponseResult getCode(@PathParam("email") String email){
        return loginService.getEmailCode(email);
    }

    @GetMapping("/forget")
    public ResponseResult forgetPassword(String email, String code, String password){
        return loginService.forgetPassword(email, code, password);
    }

}
