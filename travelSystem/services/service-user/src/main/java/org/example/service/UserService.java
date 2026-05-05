package org.example.service;

import org.example.domain.encapsulate.ResponseResult;
import org.example.domain.encapsulate.TableData;
import org.example.domain.user.User;
import org.example.domain.user.UserSelectDTO;

import java.util.List;


public interface UserService {

    ResponseResult<User> selectByEmail(String email);

    ResponseResult updatePassword(User params);

    ResponseResult<User> registerPersonal(User user);

    ResponseResult<User> selectPersonalUser();

    ResponseResult<TableData<User>> selectSystemUser(UserSelectDTO dto);

    ResponseResult<Void> updateUser(User user);

    ResponseResult<Void> deleteUser(Long id);

    ResponseResult<Void> insertUser(User user);

    ResponseResult<User> selectUserById(Long id);

}
