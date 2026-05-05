package org.example.service;

import org.example.domain.encapsulate.ResponseResult;
import org.example.domain.encapsulate.ScrollPage;

import java.util.List;
import java.util.Map;

public interface CommentService {

    ResponseResult<ScrollPage> listHotelReviews(Long hotelId, String cursor, int limit);

    ResponseResult<ScrollPage> listScenicReviews(Long scenicId, String cursor, int limit);

    ResponseResult<ScrollPage> listGuideReviews(Long guideId, String cursor, int limit);

    ResponseResult<Map<String, Object>> createGuideReview(Long guideId, String content);

    ResponseResult<Map<String, Object>> createGuideReviewReply(Long guideReviewId, Long replyUserId, String content);

    ResponseResult<Map<String, Object>> toggleGuideReviewVote(Long guideReviewId, String voteType);

    ResponseResult<Map<String, Object>> toggleGuideReviewReplyVote(Long replyId, String voteType);

    ResponseResult<Map<String, Object>> createHotelReview(Long hotelId, Long roomId, String content, String rating, List<Long> imageFileIds);

    ResponseResult<Map<String, Object>> createScenicReview(Long scenicId, String content, String rating, List<Long> imageFileIds);

    ResponseResult<Map<String, Object>> hotelAvgRatings(List<Long> hotelIds);

    ResponseResult<Map<String, Object>> scenicAvgRatings(List<Long> scenicIds);
}
