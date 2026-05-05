package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.config.mybatisplus.BatchBaseMapper;
import org.example.domain.comment.ScenicReviewImg;

@Mapper
public interface ScenicReviewImgMapper extends BatchBaseMapper<ScenicReviewImg> {
}

