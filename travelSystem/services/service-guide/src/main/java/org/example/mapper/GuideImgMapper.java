package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.config.mybatisplus.BatchBaseMapper;
import org.example.domain.guide.GuideImg;

@Mapper
public interface GuideImgMapper extends BatchBaseMapper<GuideImg> {
}

