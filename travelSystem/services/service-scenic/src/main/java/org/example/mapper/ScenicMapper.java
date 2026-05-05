package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.config.mybatisplus.BatchBaseMapper;
import org.example.domain.scenic.Scenic;

@Mapper
public interface ScenicMapper extends BatchBaseMapper<Scenic> {

    public Scenic selectScenicById(Long id);

}
