package org.example.controller;


import org.example.domain.encapsulate.ResponseResult;
import org.example.domain.encapsulate.TableData;
import org.example.domain.user.User;
import org.example.domain.user.UserSelectDTO;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/email")
    public ResponseResult<User> selectByEmail(@RequestParam("email") String email){
        return userService.selectByEmail(email);
    }

    @PostMapping("/register")
    public ResponseResult<User> register(@RequestBody User user){
        return userService.registerPersonal(user);
    }

    @PutMapping("/update/password")
    public ResponseResult updatePassword(@RequestBody User user){
        return userService.updatePassword(user);
    }

    @PutMapping("/password")
    public ResponseResult updatePasswordV2(@RequestBody User user){
        return userService.updatePassword(user);
    }

    @GetMapping("/personal")
    public ResponseResult<User> selectPersonalUser(){
        return userService.selectPersonalUser();
    }

    @GetMapping("/system")
    public ResponseResult<TableData<User>> selectSystemUser(UserSelectDTO dto){
        return userService.selectSystemUser(dto);
    }

    @GetMapping("/{id}")
    public ResponseResult<User> selectUserById(@PathVariable("id") Long id){
        return userService.selectUserById(id);
    }

    @PutMapping
    public ResponseResult<Void> updateUser(@RequestBody User user){
        return userService.updateUser(user);
    }

    @PostMapping
    public ResponseResult<Void> insertUser(@RequestBody User user){
        return userService.insertUser(user);
    }

    @DeleteMapping
    public ResponseResult<Void> deleteUser(@RequestParam("id") Long id){
        return userService.deleteUser(id);
    }
}
