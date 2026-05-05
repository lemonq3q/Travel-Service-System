package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.domain.authenticate.Role;
import org.example.domain.encapsulate.ResponseResult;
import org.example.mapper.RoleMapper;
import org.example.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public ResponseResult<List<Role>> selectAllRole() {
        List<Role> list = roleMapper.selectList(new LambdaQueryWrapper<Role>()
                .eq(Role::getIsDelete, 0)
                .eq(Role::getStatus, 1)
                .orderByAsc(Role::getId));
        return new ResponseResult<>(200, "查询成功", list);
    }
}

