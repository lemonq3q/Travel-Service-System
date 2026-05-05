package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.config.mybatisplus.BatchBaseMapper;
import org.example.domain.scenic.ScenicImg;

@Mapper
public interface ScenicImgMapper extends BatchBaseMapper<ScenicImg> {
}
