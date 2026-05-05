package org.example.controller;

import org.example.domain.encapsulate.ResponseResult;
import org.example.domain.encapsulate.ScrollPage;
import org.example.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/comment")
public class CommentController {


    @Autowired
    private CommentService commentService;

    @GetMapping("/hotel/{hotelId}")
    public ResponseResult<ScrollPage> listHotelReviews(
            @PathVariable("hotelId") Long hotelId,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return commentService.listHotelReviews(hotelId, cursor, limit);
    }

    @GetMapping("/scenic/{scenicId}")
    public ResponseResult<ScrollPage> listScenicReviews(
            @PathVariable("scenicId") Long scenicId,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return commentService.listScenicReviews(scenicId, cursor, limit);
    }

    @PostMapping("/hotel/{hotelId}")
    public ResponseResult<Map<String, Object>> createHotelReview(
            @PathVariable("hotelId") Long hotelId,
            @RequestBody Map<String, Object> body
    ) {
        String content = body == null ? null : String.valueOf(body.getOrDefault("content", ""));
        String rating = body == null ? null : String.valueOf(body.getOrDefault("rating", ""));
        List<Long> imageFileIds = parseImageFileIds(body);
        Long roomId = null;
        if (body != null) {
            Object v = body.get("roomId");
            if (v == null) v = body.get("asRoom");
            if (v == null) v = body.get("as_room");
            if (v != null) {
                try {
                    roomId = Long.valueOf(String.valueOf(v));
                } catch (Exception ignored) {
                }
            }
        }
        return commentService.createHotelReview(hotelId, roomId, content, rating, imageFileIds);
    }

    @PostMapping("/scenic/{scenicId}")
    public ResponseResult<Map<String, Object>> createScenicReview(
            @PathVariable("scenicId") Long scenicId,
            @RequestBody Map<String, Object> body
    ) {
        String content = body == null ? null : String.valueOf(body.getOrDefault("content", ""));
        String rating = body == null ? null : String.valueOf(body.getOrDefault("rating", ""));
        List<Long> imageFileIds = parseImageFileIds(body);
        return commentService.createScenicReview(scenicId, content, rating, imageFileIds);
    }

    @GetMapping("/hotel/avg")
    public ResponseResult<Map<String, Object>> hotelAvgRatings(@RequestParam(required = false) String ids) {
        List<Long> hotelIds = parseIdList(ids);
        return commentService.hotelAvgRatings(hotelIds);
    }

    @GetMapping("/scenic/avg")
    public ResponseResult<Map<String, Object>> scenicAvgRatings(@RequestParam(required = false) String ids) {
        List<Long> scenicIds = parseIdList(ids);
        return commentService.scenicAvgRatings(scenicIds);
    }

    @GetMapping("/guide/{guideId}")
    public ResponseResult<ScrollPage> listGuideReviews(
            @PathVariable("guideId") Long guideId,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return commentService.listGuideReviews(guideId, cursor, limit);
    }

    @PostMapping("/guide/{guideId}")
    public ResponseResult<Map<String, Object>> createGuideReview(
            @PathVariable("guideId") Long guideId,
            @RequestBody Map<String, Object> body
    ) {
        String content = body == null ? null : String.valueOf(body.getOrDefault("content", ""));
        return commentService.createGuideReview(guideId, content);
    }

    @PostMapping("/guide/review/{reviewId}/reply")
    public ResponseResult<Map<String, Object>> createGuideReviewReply(
            @PathVariable("reviewId") Long reviewId,
            @RequestBody Map<String, Object> body
    ) {
        String content = body == null ? null : String.valueOf(body.getOrDefault("content", ""));
        Long replyUserId = null;
        if (body != null && body.get("replyUser") != null) {
            Object replyUser = body.get("replyUser");
            if (replyUser instanceof Map<?, ?> map) {
                Object id = map.get("id");
                if (id != null) {
                    try {
                        replyUserId = Long.valueOf(String.valueOf(id));
                    } catch (Exception ignored) {
                    }
                }
            } else {
                try {
                    replyUserId = Long.valueOf(String.valueOf(replyUser));
                } catch (Exception ignored) {
                }
            }
        }
        return commentService.createGuideReviewReply(reviewId, replyUserId, content);
    }

    @PostMapping("/guide/review/{reviewId}/vote")
    public ResponseResult<Map<String, Object>> toggleGuideReviewVote(
            @PathVariable("reviewId") Long reviewId,
            @RequestBody Map<String, Object> body
    ) {
        String voteType = body == null ? null : String.valueOf(body.getOrDefault("voteType", body.getOrDefault("vote", "")));
        return commentService.toggleGuideReviewVote(reviewId, voteType);
    }

    @PostMapping("/guide/reply/{replyId}/vote")
    public ResponseResult<Map<String, Object>> toggleGuideReviewReplyVote(
            @PathVariable("replyId") Long replyId,
            @RequestBody Map<String, Object> body
    ) {
        String voteType = body == null ? null : String.valueOf(body.getOrDefault("voteType", body.getOrDefault("vote", "")));
        return commentService.toggleGuideReviewReplyVote(replyId, voteType);
    }

    private List<Long> parseIdList(String ids) {
        if (ids == null || ids.isBlank()) {
            return new ArrayList<>();
        }
        String[] parts = ids.split(",");
        List<Long> out = new ArrayList<>();
        for (String p : parts) {
            if (p == null || p.isBlank()) continue;
            try {
                out.add(Long.valueOf(p.trim()));
            } catch (Exception ignored) {
            }
        }
        return out;
    }

    private List<Long> parseImageFileIds(Map<String, Object> body) {
        if (body == null) {
            return new ArrayList<>();
        }
        Object raw = body.get("imageFileIds");
        if (raw == null) {
            raw = body.get("images");
        }
        if (raw == null) {
            raw = body.get("imgList");
        }
        List<Long> out = new ArrayList<>();
        if (raw instanceof List<?> list) {
            for (Object item : list) {
                if (item == null) continue;
                if (item instanceof Map<?, ?> m) {
                    Object id = m.get("id");
                    if (id == null) id = m.get("fileId");
                    if (id == null) continue;
                    try {
                        out.add(Long.valueOf(String.valueOf(id)));
                    } catch (Exception ignored) {
                    }
                } else {
                    try {
                        out.add(Long.valueOf(String.valueOf(item)));
                    } catch (Exception ignored) {
                    }
                }
            }
        }
        return out;
    }
}
