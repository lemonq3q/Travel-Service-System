package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.config.mybatisplus.BatchBaseMapper;
import org.example.domain.authenticate.Menu;

import java.util.List;

@Mapper
public interface MenuMapper extends BatchBaseMapper<Menu> {

    List<String> selectPermsByUserId(Long userId);
}
