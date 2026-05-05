package org.example.controller;

import org.example.domain.authenticate.Role;
import org.example.domain.encapsulate.ResponseResult;
import org.example.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping
    public ResponseResult<List<Role>> selectAllRole() {
        return roleService.selectAllRole();
    }
}

