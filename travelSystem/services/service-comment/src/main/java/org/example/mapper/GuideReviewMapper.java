package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.config.mybatisplus.BatchBaseMapper;
import org.example.domain.comment.GuideReview;

@Mapper
public interface GuideReviewMapper extends BatchBaseMapper<GuideReview> {
}

