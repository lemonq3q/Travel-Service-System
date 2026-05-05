package org.example.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.example.config.mybatisplus.BatchBaseMapper;
import org.example.domain.user.User;

import java.util.List;


@Mapper
public interface UserMapper extends BatchBaseMapper<User> {


}
