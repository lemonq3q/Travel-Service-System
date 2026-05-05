package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.config.mybatisplus.BatchBaseMapper;
import org.example.domain.guide.Guide;

@Mapper
public interface GuideMapper extends BatchBaseMapper<Guide> {
}
