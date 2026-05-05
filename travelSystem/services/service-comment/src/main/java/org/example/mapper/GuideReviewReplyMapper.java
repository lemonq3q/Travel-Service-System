package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.config.mybatisplus.BatchBaseMapper;
import org.example.domain.comment.GuideReviewReply;

@Mapper
public interface GuideReviewReplyMapper extends BatchBaseMapper<GuideReviewReply> {
}

