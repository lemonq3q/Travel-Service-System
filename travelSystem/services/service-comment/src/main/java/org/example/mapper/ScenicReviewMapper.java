package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.config.mybatisplus.BatchBaseMapper;
import org.example.domain.comment.ScenicReview;

@Mapper
public interface ScenicReviewMapper extends BatchBaseMapper<ScenicReview> {
}

