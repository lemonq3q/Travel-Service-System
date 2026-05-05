package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.config.mybatisplus.BatchBaseMapper;
import org.example.domain.authenticate.UserRole;

@Mapper
public interface UserRoleMapper extends BatchBaseMapper<UserRole> {
}
