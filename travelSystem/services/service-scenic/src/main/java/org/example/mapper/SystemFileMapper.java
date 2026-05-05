package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.config.mybatisplus.BatchBaseMapper;
import org.example.domain.SystemFile;

@Mapper
public interface SystemFileMapper extends BatchBaseMapper<SystemFile> {
}

