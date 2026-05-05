package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.config.mybatisplus.BatchBaseMapper;
import org.example.domain.authenticate.Role;

@Mapper
public interface RoleMapper extends BatchBaseMapper<Role> {
}
