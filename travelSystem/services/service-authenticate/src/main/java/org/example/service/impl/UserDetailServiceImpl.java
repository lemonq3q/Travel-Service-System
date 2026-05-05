package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.domain.authenticate.LoginUser;
import org.example.domain.user.User;
import org.example.mapper.MenuMapper;
import org.example.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>();
        queryWrapper.eq(User::getUsername, username);
        queryWrapper.eq(User::getIsDelete, 0);  //  用户未被删除
        User user = userMapper.selectOne(queryWrapper);
        if(Objects.isNull(user)){
            throw new UsernameNotFoundException("username is not exist");
        }
        List<String> list = menuMapper.selectPermsByUserId(user.getId());

        return new LoginUser(user, list);
    }
}
