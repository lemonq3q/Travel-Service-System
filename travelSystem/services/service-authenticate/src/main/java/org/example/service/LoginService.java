package org.example.service;


import org.example.domain.encapsulate.ResponseResult;
import org.example.domain.user.User;

public interface LoginService {
    ResponseResult login(User user);

    ResponseResult logout();

    ResponseResult getEmailCode(String email);

    ResponseResult forgetPassword(String email, String code, String password);

    ResponseResult<User> registerPersonal(User user);
}
