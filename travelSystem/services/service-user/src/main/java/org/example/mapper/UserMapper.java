package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.config.mybatisplus.BatchBaseMapper;
import org.example.domain.user.User;
import org.example.domain.user.UserSelectDTO;

import java.util.List;


@Mapper
public interface UserMapper extends BatchBaseMapper<User> {

    User selectPersonalUser(@Param("id") Long id);

    List<User> selectSystemUser(@Param("dto") UserSelectDTO dto);

    User selectUserById(@Param("id") Long id);
}
