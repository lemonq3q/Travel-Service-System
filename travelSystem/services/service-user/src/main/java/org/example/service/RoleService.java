package org.example.service;

import org.example.domain.authenticate.Role;
import org.example.domain.encapsulate.ResponseResult;

import java.util.List;

public interface RoleService {
    ResponseResult<List<Role>> selectAllRole();
}

