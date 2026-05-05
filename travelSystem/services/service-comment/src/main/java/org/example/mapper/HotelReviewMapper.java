package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.config.mybatisplus.BatchBaseMapper;
import org.example.domain.comment.HotelReview;

@Mapper
public interface HotelReviewMapper extends BatchBaseMapper<HotelReview> {
}
