package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.example.domain.SystemFile;
import org.example.domain.comment.GuideReview;
import org.example.domain.comment.GuideReviewReply;
import org.example.domain.comment.HotelReview;
import org.example.domain.comment.HotelReviewImg;
import org.example.domain.comment.ScenicReview;
import org.example.domain.comment.ScenicReviewImg;
import org.example.domain.encapsulate.ResponseResult;
import org.example.domain.encapsulate.ScrollPage;
import org.example.domain.hotel.HotelRoom;
import org.example.feign.FileFeignClient;
import org.example.mapper.GuideReviewMapper;
import org.example.mapper.GuideReviewReplyMapper;
import org.example.mapper.HotelReviewImgMapper;
import org.example.mapper.HotelRoomMapper;
import org.example.mapper.HotelReviewMapper;
import org.example.mapper.ScenicReviewImgMapper;
import org.example.mapper.ScenicReviewMapper;
import org.example.mapper.SystemFileMapper;
import org.example.service.CommentService;
import org.example.utils.BaseTypeUtil;
import org.example.utils.OSSUtil;
import org.example.utils.RedisCache;
import org.example.utils.SystemCommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private HotelReviewMapper hotelReviewMapper;

    @Autowired
    private HotelReviewImgMapper hotelReviewImgMapper;

    @Autowired
    private SystemFileMapper systemFileMapper;

    @Autowired
    private HotelRoomMapper hotelRoomMapper;

    @Autowired
    private ScenicReviewMapper scenicReviewMapper;

    @Autowired
    private ScenicReviewImgMapper scenicReviewImgMapper;

    @Autowired
    private OSSUtil ossUtil;

    @Autowired
    private FileFeignClient fileFeignClient;

    @Autowired
    private GuideReviewMapper guideReviewMapper;

    @Autowired
    private GuideReviewReplyMapper guideReviewReplyMapper;

    @Autowired
    private RedisCache redisCache;

    @Override
    public ResponseResult<ScrollPage> listHotelReviews(Long hotelId, String cursor, int limit) {
        if (hotelId == null) {
            return new ResponseResult<>(400, "酒店不存在");
        }
        int pageSize = limit <= 0 ? 10 : Math.min(limit, 50);

        Long cursorId = decodeCursorId(cursor);
        LambdaQueryWrapper<HotelReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HotelReview::getAsHotel, hotelId);
        wrapper.eq(HotelReview::getIsDelete, 0);
        if (cursorId != null) {
            wrapper.lt(HotelReview::getId, cursorId);
        }
        wrapper.orderByDesc(HotelReview::getId);
        wrapper.last("LIMIT " + pageSize);
        List<HotelReview> reviewList = hotelReviewMapper.selectList(wrapper);

        if (reviewList == null || reviewList.isEmpty()) {
            return new ResponseResult<>(200, "查询成功", new ScrollPage<>(new ArrayList<>(), null));
        }

        List<Long> reviewIds = reviewList.stream().map(HotelReview::getId).filter(Objects::nonNull).toList();
        List<Long> roomIds = reviewList.stream().map(HotelReview::getAsRoom).filter(Objects::nonNull).distinct().toList();

        Map<Long, String> roomNameMap = new HashMap<>();
        if (!roomIds.isEmpty()) {
            LambdaQueryWrapper<HotelRoom> roomWrapper = new LambdaQueryWrapper<>();
            roomWrapper.in(HotelRoom::getId, roomIds);
            roomWrapper.eq(HotelRoom::getIsDelete, 0);
            List<HotelRoom> rooms = hotelRoomMapper.selectList(roomWrapper);
            roomNameMap = rooms.stream()
                    .filter(r -> r != null && r.getId() != null)
                    .collect(Collectors.toMap(HotelRoom::getId, r -> r.getName() == null ? "" : r.getName(), (a, b) -> a));
        }

        Map<Long, List<Long>> reviewImgFileIdsMap = new HashMap<>();
        if (!reviewIds.isEmpty()) {
            LambdaQueryWrapper<HotelReviewImg> imgWrapper = new LambdaQueryWrapper<>();
            imgWrapper.in(HotelReviewImg::getHotelReviewId, reviewIds);
            imgWrapper.eq(HotelReviewImg::getIsDelete, 0);
            List<HotelReviewImg> imgs = hotelReviewImgMapper.selectList(imgWrapper);
            for (HotelReviewImg img : imgs) {
                if (img == null || img.getHotelReviewId() == null || img.getFileId() == null) {
                    continue;
                }
                reviewImgFileIdsMap.computeIfAbsent(img.getHotelReviewId(), k -> new ArrayList<>()).add(img.getFileId());
            }
        }

        Map<Long, String> fileIdPathMap = new HashMap<>();
        if (!reviewImgFileIdsMap.isEmpty()) {
            List<Long> fileIds = reviewImgFileIdsMap.values().stream().flatMap(Collection::stream).distinct().toList();
            if (!fileIds.isEmpty()) {
                LambdaQueryWrapper<SystemFile> fileWrapper = new LambdaQueryWrapper<>();
                fileWrapper.in(SystemFile::getId, fileIds);
                fileWrapper.eq(SystemFile::getIsDelete, 0);
                List<SystemFile> files = systemFileMapper.selectList(fileWrapper);
                fileIdPathMap = files.stream()
                        .filter(f -> f != null && f.getId() != null && BaseTypeUtil.hasText(f.getPath()))
                        .collect(Collectors.toMap(SystemFile::getId, SystemFile::getPath, (a, b) -> a));
            }
        }

        List<Map<String, Object>> items = new ArrayList<>();
        for (HotelReview review : reviewList) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", String.valueOf(review.getId()));
            m.put("hotelId", String.valueOf(review.getAsHotel()));

            Map<String, Object> user = new HashMap<>();
            user.put("id", review.getAsUser() == null ? "" : String.valueOf(review.getAsUser()));
            user.put("name", review.getAsUser() == null ? "匿名用户" : ("用户" + review.getAsUser()));
            user.put("avatarUrl", "");
            m.put("user", user);

            Map<String, Object> room = new HashMap<>();
            room.put("id", review.getAsRoom() == null ? "" : String.valueOf(review.getAsRoom()));
            room.put("name", review.getAsRoom() == null ? "" : roomNameMap.getOrDefault(review.getAsRoom(), ""));
            m.put("room", room);

            m.put("rating", review.getRating() == null ? 0 : review.getRating());
            m.put("content", review.getContent() == null ? "" : review.getContent());
            m.put("createTime", toIso(review.getCreateTime()));

            List<Long> imgFileIds = reviewImgFileIdsMap.getOrDefault(review.getId(), new ArrayList<>());
            List<String> imgUrls = new ArrayList<>();
            for (Long fileId : imgFileIds) {
                String path = fileIdPathMap.get(fileId);
                if (BaseTypeUtil.hasText(path)) {
                    imgUrls.add(ossUtil.getTmpUrl(path));
                }
            }
            m.put("images", imgUrls);

            items.add(m);
        }

        String nextCursor = null;
        if (reviewList.size() == pageSize) {
            HotelReview last = reviewList.get(reviewList.size() - 1);
            nextCursor = encodeCursorId(last.getId());
        }

        return new ResponseResult<>(200, "查询成功", new ScrollPage<>(items, nextCursor));
    }

    @Override
    public ResponseResult<ScrollPage> listScenicReviews(Long scenicId, String cursor, int limit) {
        if (scenicId == null) {
            return new ResponseResult<>(400, "景区不存在");
        }
        int pageSize = limit <= 0 ? 10 : Math.min(limit, 50);

        Long cursorId = decodeCursorId(cursor);
        LambdaQueryWrapper<ScenicReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScenicReview::getAsScenic, scenicId);
        wrapper.eq(ScenicReview::getIsDelete, 0);
        if (cursorId != null) {
            wrapper.lt(ScenicReview::getId, cursorId);
        }
        wrapper.orderByDesc(ScenicReview::getId);
        wrapper.last("LIMIT " + pageSize);
        List<ScenicReview> reviewList = scenicReviewMapper.selectList(wrapper);

        if (reviewList == null || reviewList.isEmpty()) {
            return new ResponseResult<>(200, "查询成功", new ScrollPage<>(new ArrayList<>(), null));
        }

        List<Long> reviewIds = reviewList.stream().map(ScenicReview::getId).filter(Objects::nonNull).toList();

        Map<Long, List<Long>> reviewImgFileIdsMap = new HashMap<>();
        if (!reviewIds.isEmpty()) {
            LambdaQueryWrapper<ScenicReviewImg> imgWrapper = new LambdaQueryWrapper<>();
            imgWrapper.in(ScenicReviewImg::getScenicReviewId, reviewIds);
            imgWrapper.eq(ScenicReviewImg::getIsDelete, 0);
            List<ScenicReviewImg> imgs = scenicReviewImgMapper.selectList(imgWrapper);
            for (ScenicReviewImg img : imgs) {
                if (img == null || img.getScenicReviewId() == null || img.getFileId() == null) {
                    continue;
                }
                reviewImgFileIdsMap.computeIfAbsent(img.getScenicReviewId(), k -> new ArrayList<>()).add(img.getFileId());
            }
        }

        Map<Long, String> fileIdPathMap = new HashMap<>();
        if (!reviewImgFileIdsMap.isEmpty()) {
            List<Long> fileIds = reviewImgFileIdsMap.values().stream().flatMap(Collection::stream).distinct().toList();
            if (!fileIds.isEmpty()) {
                LambdaQueryWrapper<SystemFile> fileWrapper = new LambdaQueryWrapper<>();
                fileWrapper.in(SystemFile::getId, fileIds);
                fileWrapper.eq(SystemFile::getIsDelete, 0);
                List<SystemFile> files = systemFileMapper.selectList(fileWrapper);
                fileIdPathMap = files.stream()
                        .filter(f -> f != null && f.getId() != null && BaseTypeUtil.hasText(f.getPath()))
                        .collect(Collectors.toMap(SystemFile::getId, SystemFile::getPath, (a, b) -> a));
            }
        }

        List<Map<String, Object>> items = new ArrayList<>();
        for (ScenicReview review : reviewList) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", String.valueOf(review.getId()));
            m.put("scenicId", String.valueOf(review.getAsScenic()));

            Map<String, Object> user = new HashMap<>();
            user.put("id", review.getAsUser() == null ? "" : String.valueOf(review.getAsUser()));
            user.put("name", review.getAsUser() == null ? "匿名用户" : ("用户" + review.getAsUser()));
            user.put("avatarUrl", "");
            m.put("user", user);

            m.put("rating", review.getRating() == null ? 0 : review.getRating());
            m.put("content", review.getContent() == null ? "" : review.getContent());
            m.put("createTime", toIso(review.getCreateTime()));

            List<Long> imgFileIds = reviewImgFileIdsMap.getOrDefault(review.getId(), new ArrayList<>());
            List<String> imgUrls = new ArrayList<>();
            for (Long fileId : imgFileIds) {
                String path = fileIdPathMap.get(fileId);
                if (BaseTypeUtil.hasText(path)) {
                    imgUrls.add(ossUtil.getTmpUrl(path));
                }
            }
            m.put("images", imgUrls);

            items.add(m);
        }

        String nextCursor = null;
        if (reviewList.size() == pageSize) {
            ScenicReview last = reviewList.get(reviewList.size() - 1);
            nextCursor = encodeCursorId(last.getId());
        }

        return new ResponseResult<>(200, "查询成功", new ScrollPage<>(items, nextCursor));
    }

    @Override
    public ResponseResult<ScrollPage> listGuideReviews(Long guideId, String cursor, int limit) {
        if (guideId == null) {
            return new ResponseResult<>(400, "攻略不存在");
        }
        int pageSize = limit <= 0 ? 10 : Math.min(limit, 50);
        Long cursorId = decodeCursorId(cursor);
        Long nowUserId = SystemCommonUtil.getNowUserId();

        LambdaQueryWrapper<GuideReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GuideReview::getAsGuide, guideId);
        wrapper.eq(GuideReview::getIsDelete, 0);
        if (cursorId != null) {
            wrapper.lt(GuideReview::getId, cursorId);
        }
        wrapper.orderByDesc(GuideReview::getId);
        wrapper.last("LIMIT " + pageSize);
        List<GuideReview> reviewList = guideReviewMapper.selectList(wrapper);
        if (reviewList == null || reviewList.isEmpty()) {
            return new ResponseResult<>(200, "查询成功", new ScrollPage<>(new ArrayList<>(), null));
        }

        List<Long> reviewIds = reviewList.stream().map(GuideReview::getId).filter(Objects::nonNull).toList();

        Map<Long, List<GuideReviewReply>> replyMap = new HashMap<>();
        if (!reviewIds.isEmpty()) {
            LambdaQueryWrapper<GuideReviewReply> replyWrapper = new LambdaQueryWrapper<>();
            replyWrapper.in(GuideReviewReply::getAsGuideReview, reviewIds);
            replyWrapper.eq(GuideReviewReply::getIsDelete, 0);
            replyWrapper.orderByAsc(GuideReviewReply::getId);
            List<GuideReviewReply> replies = guideReviewReplyMapper.selectList(replyWrapper);
            if (replies != null) {
                for (GuideReviewReply r : replies) {
                    if (r == null || r.getAsGuideReview() == null) {
                        continue;
                    }
                    replyMap.computeIfAbsent(r.getAsGuideReview(), k -> new ArrayList<>()).add(r);
                }
            }
        }

        List<Map<String, Object>> items = new ArrayList<>();
        for (GuideReview review : reviewList) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", String.valueOf(review.getId()));
            m.put("asGuide", review.getAsGuide() == null ? 0 : review.getAsGuide());

            Map<String, Object> user = new HashMap<>();
            user.put("id", review.getAsUser() == null ? "" : String.valueOf(review.getAsUser()));
            user.put("name", review.getAsUser() == null ? "匿名用户" : ("用户" + review.getAsUser()));
            user.put("avatarUrl", "");
            m.put("asUser", user);

            m.put("content", review.getContent() == null ? "" : review.getContent());
            m.put("likeCount", review.getLikeCount() == null ? 0 : review.getLikeCount());
            m.put("dislikeCount", review.getDislikeCount() == null ? 0 : review.getDislikeCount());
            m.put("voted", getUserVote("vote:guideReview", review.getId(), nowUserId));
            m.put("createTime", toIso(review.getCreateTime()));

            List<GuideReviewReply> replies = replyMap.getOrDefault(review.getId(), new ArrayList<>());
            List<Map<String, Object>> replyDtos = new ArrayList<>();
            for (GuideReviewReply rr : replies) {
                Map<String, Object> rm = new HashMap<>();
                rm.put("id", String.valueOf(rr.getId()));
                rm.put("asGuideReview", rr.getAsGuideReview() == null ? "" : String.valueOf(rr.getAsGuideReview()));

                Map<String, Object> ru = new HashMap<>();
                ru.put("id", rr.getAsUser() == null ? "" : String.valueOf(rr.getAsUser()));
                ru.put("name", rr.getAsUser() == null ? "匿名用户" : ("用户" + rr.getAsUser()));
                ru.put("avatarUrl", "");
                rm.put("asUser", ru);

                Map<String, Object> replyUser = new HashMap<>();
                replyUser.put("id", rr.getReplyUser() == null ? "" : String.valueOf(rr.getReplyUser()));
                replyUser.put("name", rr.getReplyUser() == null ? "" : ("用户" + rr.getReplyUser()));
                rm.put("replyUser", replyUser);

                rm.put("content", rr.getContent() == null ? "" : rr.getContent());
                rm.put("likeCount", rr.getLikeCount() == null ? 0 : rr.getLikeCount());
                rm.put("dislikeCount", rr.getDislikeCount() == null ? 0 : rr.getDislikeCount());
                rm.put("voted", getUserVote("vote:guideReply", rr.getId(), nowUserId));
                rm.put("createTime", toIso(rr.getCreateTime()));
                replyDtos.add(rm);
            }
            m.put("replies", replyDtos);

            items.add(m);
        }

        String nextCursor = null;
        if (reviewList.size() == pageSize) {
            GuideReview last = reviewList.get(reviewList.size() - 1);
            nextCursor = encodeCursorId(last.getId());
        }
        return new ResponseResult<>(200, "查询成功", new ScrollPage<>(items, nextCursor));
    }

    @Override
    public ResponseResult<Map<String, Object>> createGuideReview(Long guideId, String content) {
        if (guideId == null) {
            return new ResponseResult<>(400, "攻略不存在");
        }
        if (!BaseTypeUtil.hasText(content)) {
            return new ResponseResult<>(400, "内容不能为空");
        }
        Long nowUserId = SystemCommonUtil.getNowUserId();
        long now = System.currentTimeMillis();

        GuideReview review = new GuideReview();
        review.setAsGuide(guideId);
        review.setAsUser(nowUserId);
        review.setContent(content);
        review.setLikeCount(0);
        review.setDislikeCount(0);
        review.setCreateTime(now);
        review.setUpdateTime(now);
        review.setUpdateBy(nowUserId);
        review.setIsDelete(0);
        guideReviewMapper.insert(review);

        Map<String, Object> data = new HashMap<>();
        data.put("id", String.valueOf(review.getId()));
        data.put("asGuide", guideId);
        Map<String, Object> user = new HashMap<>();
        user.put("id", nowUserId == null ? "" : String.valueOf(nowUserId));
        user.put("name", nowUserId == null ? "匿名用户" : ("用户" + nowUserId));
        user.put("avatarUrl", "");
        data.put("asUser", user);
        data.put("content", review.getContent());
        data.put("likeCount", 0);
        data.put("dislikeCount", 0);
        data.put("voted", null);
        data.put("createTime", toIso(review.getCreateTime()));
        data.put("replies", new ArrayList<>());
        return new ResponseResult<>(200, "发表成功", data);
    }

    @Override
    public ResponseResult<Map<String, Object>> createGuideReviewReply(Long guideReviewId, Long replyUserId, String content) {
        if (guideReviewId == null) {
            return new ResponseResult<>(400, "评论不存在");
        }
        if (!BaseTypeUtil.hasText(content)) {
            return new ResponseResult<>(400, "内容不能为空");
        }
        Long nowUserId = SystemCommonUtil.getNowUserId();
        long now = System.currentTimeMillis();

        LambdaQueryWrapper<GuideReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GuideReview::getId, guideReviewId);
        wrapper.eq(GuideReview::getIsDelete, 0);
        GuideReview review = guideReviewMapper.selectOne(wrapper);
        if (review == null) {
            return new ResponseResult<>(404, "评论不存在");
        }

        GuideReviewReply reply = new GuideReviewReply();
        reply.setAsGuideReview(guideReviewId);
        reply.setAsUser(nowUserId);
        reply.setReplyUser(replyUserId);
        reply.setContent(content);
        reply.setLikeCount(0);
        reply.setDislikeCount(0);
        reply.setCreateTime(now);
        reply.setUpdateTime(now);
        reply.setUpdateBy(nowUserId);
        reply.setIsDelete(0);
        guideReviewReplyMapper.insert(reply);

        Map<String, Object> data = new HashMap<>();
        data.put("id", String.valueOf(reply.getId()));
        data.put("asGuideReview", String.valueOf(guideReviewId));
        Map<String, Object> user = new HashMap<>();
        user.put("id", nowUserId == null ? "" : String.valueOf(nowUserId));
        user.put("name", nowUserId == null ? "匿名用户" : ("用户" + nowUserId));
        user.put("avatarUrl", "");
        data.put("asUser", user);
        Map<String, Object> replyUser = new HashMap<>();
        replyUser.put("id", replyUserId == null ? "" : String.valueOf(replyUserId));
        replyUser.put("name", replyUserId == null ? "" : ("用户" + replyUserId));
        data.put("replyUser", replyUser);
        data.put("content", reply.getContent());
        data.put("likeCount", 0);
        data.put("dislikeCount", 0);
        data.put("voted", null);
        data.put("createTime", toIso(reply.getCreateTime()));
        return new ResponseResult<>(200, "回复成功", data);
    }

    @Override
    public ResponseResult<Map<String, Object>> toggleGuideReviewVote(Long guideReviewId, String voteType) {
        if (guideReviewId == null) {
            return new ResponseResult<>(400, "评论不存在");
        }
        if (!"like".equals(voteType) && !"dislike".equals(voteType)) {
            return new ResponseResult<>(400, "参数错误");
        }
        Long nowUserId = SystemCommonUtil.getNowUserId();
        if (nowUserId == null) {
            nowUserId = 0L;
        }

        LambdaQueryWrapper<GuideReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GuideReview::getId, guideReviewId);
        wrapper.eq(GuideReview::getIsDelete, 0);
        GuideReview review = guideReviewMapper.selectOne(wrapper);
        if (review == null) {
            return new ResponseResult<>(404, "评论不存在");
        }

        String key = buildVoteKey("vote:guideReview", guideReviewId, nowUserId);
        String current = redisCache.getCacheObject(key);

        int like = review.getLikeCount() == null ? 0 : review.getLikeCount();
        int dislike = review.getDislikeCount() == null ? 0 : review.getDislikeCount();
        String nextVote;
        if ("like".equals(voteType)) {
            if ("like".equals(current)) {
                like = Math.max(0, like - 1);
                nextVote = null;
            } else {
                if ("dislike".equals(current)) {
                    dislike = Math.max(0, dislike - 1);
                }
                like = like + 1;
                nextVote = "like";
            }
        } else {
            if ("dislike".equals(current)) {
                dislike = Math.max(0, dislike - 1);
                nextVote = null;
            } else {
                if ("like".equals(current)) {
                    like = Math.max(0, like - 1);
                }
                dislike = dislike + 1;
                nextVote = "dislike";
            }
        }

        LambdaUpdateWrapper<GuideReview> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(GuideReview::getId, guideReviewId);
        updateWrapper.eq(GuideReview::getIsDelete, 0);
        updateWrapper.set(GuideReview::getLikeCount, like);
        updateWrapper.set(GuideReview::getDislikeCount, dislike);
        updateWrapper.set(GuideReview::getUpdateTime, System.currentTimeMillis());
        updateWrapper.set(GuideReview::getUpdateBy, nowUserId);
        guideReviewMapper.update(null, updateWrapper);

        if (nextVote == null) {
            redisCache.deleteObject(key);
        } else {
            redisCache.setCacheObject(key, nextVote);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", String.valueOf(review.getId()));
        data.put("likeCount", like);
        data.put("dislikeCount", dislike);
        data.put("voted", nextVote);
        return new ResponseResult<>(200, "操作成功", data);
    }

    @Override
    public ResponseResult<Map<String, Object>> toggleGuideReviewReplyVote(Long replyId, String voteType) {
        if (replyId == null) {
            return new ResponseResult<>(400, "回复不存在");
        }
        if (!"like".equals(voteType) && !"dislike".equals(voteType)) {
            return new ResponseResult<>(400, "参数错误");
        }
        Long nowUserId = SystemCommonUtil.getNowUserId();
        if (nowUserId == null) {
            nowUserId = 0L;
        }

        LambdaQueryWrapper<GuideReviewReply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GuideReviewReply::getId, replyId);
        wrapper.eq(GuideReviewReply::getIsDelete, 0);
        GuideReviewReply reply = guideReviewReplyMapper.selectOne(wrapper);
        if (reply == null) {
            return new ResponseResult<>(404, "回复不存在");
        }

        String key = buildVoteKey("vote:guideReply", replyId, nowUserId);
        String current = redisCache.getCacheObject(key);

        int like = reply.getLikeCount() == null ? 0 : reply.getLikeCount();
        int dislike = reply.getDislikeCount() == null ? 0 : reply.getDislikeCount();
        String nextVote;
        if ("like".equals(voteType)) {
            if ("like".equals(current)) {
                like = Math.max(0, like - 1);
                nextVote = null;
            } else {
                if ("dislike".equals(current)) {
                    dislike = Math.max(0, dislike - 1);
                }
                like = like + 1;
                nextVote = "like";
            }
        } else {
            if ("dislike".equals(current)) {
                dislike = Math.max(0, dislike - 1);
                nextVote = null;
            } else {
                if ("like".equals(current)) {
                    like = Math.max(0, like - 1);
                }
                dislike = dislike + 1;
                nextVote = "dislike";
            }
        }

        LambdaUpdateWrapper<GuideReviewReply> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(GuideReviewReply::getId, replyId);
        updateWrapper.eq(GuideReviewReply::getIsDelete, 0);
        updateWrapper.set(GuideReviewReply::getLikeCount, like);
        updateWrapper.set(GuideReviewReply::getDislikeCount, dislike);
        updateWrapper.set(GuideReviewReply::getUpdateTime, System.currentTimeMillis());
        updateWrapper.set(GuideReviewReply::getUpdateBy, nowUserId);
        guideReviewReplyMapper.update(null, updateWrapper);

        if (nextVote == null) {
            redisCache.deleteObject(key);
        } else {
            redisCache.setCacheObject(key, nextVote);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", String.valueOf(reply.getId()));
        data.put("likeCount", like);
        data.put("dislikeCount", dislike);
        data.put("voted", nextVote);
        return new ResponseResult<>(200, "操作成功", data);
    }

    @Override
    public ResponseResult<Map<String, Object>> createHotelReview(Long hotelId, Long roomId, String content, String rating, List<Long> imageFileIds) {
        if (hotelId == null) {
            return new ResponseResult<>(400, "酒店不存在");
        }
        Long nowUserId = SystemCommonUtil.getNowUserId();
        if (nowUserId == null || nowUserId <= 0) {
            return new ResponseResult<>(401, "未登录");
        }
        BigDecimal r;
        try {
            r = rating == null ? null : new BigDecimal(String.valueOf(rating));
        } catch (Exception e) {
            r = null;
        }
        if (r == null) {
            return new ResponseResult<>(400, "评分不合法");
        }
        if (r.compareTo(BigDecimal.ZERO) < 0) r = BigDecimal.ZERO;
        if (r.compareTo(new BigDecimal("5")) > 0) r = new BigDecimal("5");
        String c = content == null ? "" : String.valueOf(content).trim();
        if (c.isBlank()) {
            return new ResponseResult<>(400, "评论内容不能为空");
        }
        long now = System.currentTimeMillis();
        HotelReview review = new HotelReview();
        review.setAsHotel(hotelId);
        review.setAsUser(nowUserId);
        review.setAsRoom(roomId);
        review.setContent(c);
        review.setRating(r);
        review.setCreateTime(now);
        review.setUpdateTime(now);
        review.setUpdateBy(nowUserId);
        review.setIsDelete(0);
        hotelReviewMapper.insert(review);

        List<Long> ids = normalizeIdList(imageFileIds);
        if (!ids.isEmpty()) {
            long now2 = System.currentTimeMillis();
            List<HotelReviewImg> imgs = new ArrayList<>();
            for (Long fileId : ids) {
                if (fileId == null) continue;
                HotelReviewImg img = new HotelReviewImg();
                img.setHotelReviewId(review.getId());
                img.setFileId(fileId);
                img.setCreateTime(now2);
                img.setUpdateTime(now2);
                img.setUpdateBy(nowUserId);
                img.setIsDelete(0);
                imgs.add(img);
            }
            if (!imgs.isEmpty()) {
                hotelReviewImgMapper.insertBatchSomeColumn(imgs);
                try {
                    fileFeignClient.addFileLink(ids);
                } catch (Exception ignored) {
                }
            }
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", String.valueOf(review.getId()));
        return new ResponseResult<>(200, "ok", data);
    }

    @Override
    public ResponseResult<Map<String, Object>> createScenicReview(Long scenicId, String content, String rating, List<Long> imageFileIds) {
        if (scenicId == null) {
            return new ResponseResult<>(400, "景区不存在");
        }
        Long nowUserId = SystemCommonUtil.getNowUserId();
        if (nowUserId == null || nowUserId <= 0) {
            return new ResponseResult<>(401, "未登录");
        }
        BigDecimal r;
        try {
            r = rating == null ? null : new BigDecimal(String.valueOf(rating));
        } catch (Exception e) {
            r = null;
        }
        if (r == null) {
            return new ResponseResult<>(400, "评分不合法");
        }
        if (r.compareTo(BigDecimal.ZERO) < 0) r = BigDecimal.ZERO;
        if (r.compareTo(new BigDecimal("5")) > 0) r = new BigDecimal("5");
        String c = content == null ? "" : String.valueOf(content).trim();
        if (c.isBlank()) {
            return new ResponseResult<>(400, "评论内容不能为空");
        }
        long now = System.currentTimeMillis();
        ScenicReview review = new ScenicReview();
        review.setAsScenic(scenicId);
        review.setAsUser(nowUserId);
        review.setContent(c);
        review.setRating(r);
        review.setCreateTime(now);
        review.setUpdateTime(now);
        review.setUpdateBy(nowUserId);
        review.setIsDelete(0);
        scenicReviewMapper.insert(review);

        List<Long> ids = normalizeIdList(imageFileIds);
        if (!ids.isEmpty()) {
            long now2 = System.currentTimeMillis();
            List<ScenicReviewImg> imgs = new ArrayList<>();
            for (Long fileId : ids) {
                if (fileId == null) continue;
                ScenicReviewImg img = new ScenicReviewImg();
                img.setScenicReviewId(review.getId());
                img.setFileId(fileId);
                img.setCreateTime(now2);
                img.setUpdateTime(now2);
                img.setUpdateBy(nowUserId);
                img.setIsDelete(0);
                imgs.add(img);
            }
            if (!imgs.isEmpty()) {
                scenicReviewImgMapper.insertBatchSomeColumn(imgs);
                try {
                    fileFeignClient.addFileLink(ids);
                } catch (Exception ignored) {
                }
            }
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", String.valueOf(review.getId()));
        return new ResponseResult<>(200, "ok", data);
    }

    @Override
    public ResponseResult<Map<String, Object>> hotelAvgRatings(List<Long> hotelIds) {
        QueryWrapper<HotelReview> wrapper = new QueryWrapper<>();
        wrapper.eq("is_delete", 0);
        if (hotelIds != null && !hotelIds.isEmpty()) {
            wrapper.in("as_hotel", hotelIds);
        }
        wrapper.select("as_hotel as asHotel", "AVG(rating) AS avgRating");
        wrapper.groupBy("as_hotel");

        List<Map<String, Object>> rows = hotelReviewMapper.selectMaps(wrapper);
        List<Map<String, Object>> out = new ArrayList<>();
        if (rows != null) {
            for (Map<String, Object> row : rows) {
                if (row == null) continue;
                Object id = row.get("asHotel");
                if (id == null) id = row.get("as_hotel");
                Object avg = row.get("avgRating");
                Map<String, Object> m = new HashMap<>();
                m.put("id", id == null ? null : String.valueOf(id));
                m.put("rating", avg == null ? null : String.valueOf(avg));
                out.add(m);
            }
        }
        Map<String, Object> data = new HashMap<>();
        data.put("list", out);
        return new ResponseResult<>(200, "ok", data);
    }

    @Override
    public ResponseResult<Map<String, Object>> scenicAvgRatings(List<Long> scenicIds) {
        QueryWrapper<ScenicReview> wrapper = new QueryWrapper<>();
        wrapper.eq("is_delete", 0);
        if (scenicIds != null && !scenicIds.isEmpty()) {
            wrapper.in("as_scenic", scenicIds);
        }
        wrapper.select("as_scenic as asScenic", "AVG(rating) AS avgRating");
        wrapper.groupBy("as_scenic");

        List<Map<String, Object>> rows = scenicReviewMapper.selectMaps(wrapper);
        List<Map<String, Object>> out = new ArrayList<>();
        if (rows != null) {
            for (Map<String, Object> row : rows) {
                if (row == null) continue;
                Object id = row.get("asScenic");
                if (id == null) id = row.get("as_scenic");
                Object avg = row.get("avgRating");
                Map<String, Object> m = new HashMap<>();
                m.put("id", id == null ? null : String.valueOf(id));
                m.put("rating", avg == null ? null : String.valueOf(avg));
                out.add(m);
            }
        }
        Map<String, Object> data = new HashMap<>();
        data.put("list", out);
        return new ResponseResult<>(200, "ok", data);
    }

    private String getUserVote(String prefix, Long id, Long userId) {
        if (id == null || userId == null) {
            return null;
        }
        String key = buildVoteKey(prefix, id, userId);
        try {
            return redisCache.getCacheObject(key);
        } catch (Exception e) {
            return null;
        }
    }

    private List<Long> normalizeIdList(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        LinkedHashSet<Long> set = new LinkedHashSet<>();
        for (Long id : ids) {
            if (id == null || id <= 0) continue;
            set.add(id);
            if (set.size() >= 9) {
                break;
            }
        }
        return new ArrayList<>(set);
    }

    private static String buildVoteKey(String prefix, Long id, Long userId) {
        return prefix + ":" + id + ":" + userId;
    }

    private static String toIso(Long epochMillis) {
        if (epochMillis == null || epochMillis <= 0) {
            return "";
        }
        return Instant.ofEpochMilli(epochMillis).toString();
    }

    private static String encodeCursorId(Long id) {
        if (id == null) {
            return null;
        }
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(String.valueOf(id).getBytes(StandardCharsets.UTF_8));
    }

    private static Long decodeCursorId(String cursor) {
        if (!BaseTypeUtil.hasText(cursor)) {
            return null;
        }
        try {
            byte[] decoded = Base64.getUrlDecoder().decode(cursor);
            String s = new String(decoded, StandardCharsets.UTF_8);
            if (!BaseTypeUtil.hasText(s)) {
                return null;
            }
            return Long.valueOf(s.trim());
        } catch (Exception e) {
            return null;
        }
    }
}
