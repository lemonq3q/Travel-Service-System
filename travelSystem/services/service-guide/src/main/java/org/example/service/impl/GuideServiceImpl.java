package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.example.domain.SystemFile;
import org.example.domain.encapsulate.ResponseResult;
import org.example.domain.encapsulate.ScrollPage;
import org.example.domain.guide.Guide;
import org.example.domain.guide.GuideImg;
import org.example.domain.guide.dto.GuideImageDTO;
import org.example.domain.guide.dto.GuideUpsertRequest;
import org.example.feign.FileFeignClient;
import org.example.handler.exception.BusinessException;
import org.example.mapper.GuideMapper;
import org.example.mapper.GuideImgMapper;
import org.example.mapper.SystemFileMapper;
import org.example.service.GuideService;
import org.example.utils.BaseTypeUtil;
import org.example.utils.OSSUtil;
import org.example.utils.SystemCommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GuideServiceImpl implements GuideService {

    @Autowired
    private GuideMapper guideMapper;

    @Autowired
    private GuideImgMapper guideImgMapper;

    @Autowired
    private SystemFileMapper systemFileMapper;

    @Autowired
    private FileFeignClient fileFeignClient;

    @Autowired
    private OSSUtil ossUtil;

    @Override
    public ResponseResult<ScrollPage> publicSearchGuides(String cursor, int limit, String keyword) {
        int pageSize = limit <= 0 ? 9 : Math.min(limit, 50);
        Long cursorId = decodeCursorId(cursor);

        LambdaQueryWrapper<Guide> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Guide::getIsDelete, 0);
        if (BaseTypeUtil.hasText(keyword)) {
            wrapper.and(w -> w.like(Guide::getTitle, keyword).or().like(Guide::getContent, keyword));
        }
        if (cursorId != null) {
            wrapper.lt(Guide::getId, cursorId);
        }
        wrapper.orderByDesc(Guide::getId);
        wrapper.last("LIMIT " + pageSize);

        List<Guide> guideList = guideMapper.selectList(wrapper);
        if (guideList == null || guideList.isEmpty()) {
            return new ResponseResult<>(200, "查询成功", new ScrollPage<>(new ArrayList<>(), null));
        }

        List<Long> guideIds = guideList.stream().map(Guide::getId).filter(Objects::nonNull).toList();
        Map<Long, String> coverUrlMap = buildGuideCoverUrlMap(guideIds);

        List<Map<String, Object>> items = guideList.stream().map(g -> {
            Map<String, Object> item = new HashMap<>();
            item.put("id", g.getId());
            item.put("title", g.getTitle() == null ? "" : g.getTitle());
            item.put("coverUrl", coverUrlMap.getOrDefault(g.getId(), ""));
            Map<String, Object> author = new HashMap<>();
            author.put("id", g.getAsUser() == null ? "" : String.valueOf(g.getAsUser()));
            author.put("name", g.getAsUser() == null ? "匿名" : ("用户" + g.getAsUser()));
            author.put("avatarUrl", "");
            item.put("author", author);
            item.put("createTime", toIso(g.getCreateTime()));
            return item;
        }).toList();

        String nextCursor = null;
        if (guideList.size() == pageSize) {
            Guide last = guideList.get(guideList.size() - 1);
            nextCursor = encodeCursorId(last.getId());
        }
        return new ResponseResult<>(200, "查询成功", new ScrollPage<>(items, nextCursor));
    }

    @Override
    public ResponseResult<Map<String, Object>> publicGetGuideDetail(Long id) {
        return buildGuideDetail(id, null);
    }

    @Override
    public ResponseResult<ScrollPage> listMyGuides(String cursor, int limit) {
        Long nowUserId = SystemCommonUtil.getNowUserId();
        int pageSize = limit <= 0 ? 10 : Math.min(limit, 50);
        Long cursorId = decodeCursorId(cursor);

        LambdaQueryWrapper<Guide> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Guide::getIsDelete, 0);
        wrapper.eq(Guide::getAsUser, nowUserId);
        if (cursorId != null) {
            wrapper.lt(Guide::getId, cursorId);
        }
        wrapper.orderByDesc(Guide::getId);
        wrapper.last("LIMIT " + pageSize);

        List<Guide> guideList = guideMapper.selectList(wrapper);
        if (guideList == null || guideList.isEmpty()) {
            return new ResponseResult<>(200, "查询成功", new ScrollPage<>(new ArrayList<>(), null));
        }

        List<Long> guideIds = guideList.stream().map(Guide::getId).filter(Objects::nonNull).toList();
        Map<Long, String> coverUrlMap = buildGuideCoverUrlMap(guideIds);

        List<Map<String, Object>> items = guideList.stream().map(g -> {
            Map<String, Object> item = new HashMap<>();
            item.put("id", g.getId());
            item.put("title", g.getTitle() == null ? "" : g.getTitle());
            item.put("coverUrl", coverUrlMap.getOrDefault(g.getId(), ""));
            item.put("createTime", toIso(g.getCreateTime()));
            item.put("updateTime", toIso(g.getUpdateTime()));
            return item;
        }).toList();

        String nextCursor = null;
        if (guideList.size() == pageSize) {
            Guide last = guideList.get(guideList.size() - 1);
            nextCursor = encodeCursorId(last.getId());
        }
        return new ResponseResult<>(200, "查询成功", new ScrollPage<>(items, nextCursor));
    }

    @Override
    public ResponseResult<Map<String, Object>> getMyGuideDetail(Long id) {
        Long nowUserId = SystemCommonUtil.getNowUserId();
        return buildGuideDetail(id, nowUserId);
    }

    @Override
    @Transactional
    public ResponseResult<Map<String, Object>> createGuide(GuideUpsertRequest request) {
        Long nowUserId = SystemCommonUtil.getNowUserId();
        long now = System.currentTimeMillis();

        if (request == null || !BaseTypeUtil.hasText(request.getTitle()) || !BaseTypeUtil.hasText(request.getContent())) {
            return new ResponseResult<>(400, "标题和内容不能为空");
        }

        Guide guide = new Guide();
        guide.setAsUser(nowUserId);
        guide.setTitle(request.getTitle().trim());
        guide.setContent(request.getContent());
        guide.setCreateTime(now);
        guide.setUpdateTime(now);
        guide.setUpdateBy(nowUserId);
        guide.setIsDelete(0);
        guideMapper.insert(guide);

        List<GuideImageDTO> images = normalizeImages(request);
        if (images != null && !images.isEmpty()) {
            List<Long> fileIds = images.stream()
                    .map(GuideImageDTO::getFileId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();

            List<GuideImg> bindList = images.stream()
                    .filter(i -> i != null && i.getFileId() != null)
                    .map(i -> {
                        GuideImg img = new GuideImg();
                        img.setAsId(guide.getId());
                        img.setFileId(i.getFileId());
                        img.setSortIndex(i.getSortIndex() == null ? 0 : i.getSortIndex());
                        img.setCreateTime(now);
                        img.setUpdateTime(now);
                        img.setUpdateBy(nowUserId);
                        img.setIsDelete(0);
                        return img;
                    })
                    .toList();

            if (!bindList.isEmpty()) {
                guideImgMapper.insertBatchSomeColumn(bindList);
            }

            if (!fileIds.isEmpty()) {
                ResponseResult<Void> res = fileFeignClient.addFileLink(fileIds);
                if (res == null || res.getCode() == null || !res.getCode().equals(200)) {
                    throw new BusinessException(500, "图片绑定失败");
                }
            }
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", guide.getId());
        return new ResponseResult<>(200, "发布成功", data);
    }

    @Override
    @Transactional
    public ResponseResult<Void> updateGuide(Long id, GuideUpsertRequest request) {
        if (id == null) {
            return new ResponseResult<>(400, "攻略不存在");
        }
        Long nowUserId = SystemCommonUtil.getNowUserId();
        long now = System.currentTimeMillis();

        LambdaQueryWrapper<Guide> existsWrapper = new LambdaQueryWrapper<>();
        existsWrapper.eq(Guide::getId, id);
        existsWrapper.eq(Guide::getIsDelete, 0);
        Guide oldGuide = guideMapper.selectOne(existsWrapper);
        if (oldGuide == null) {
            return new ResponseResult<>(400, "攻略不存在");
        }
        if (oldGuide.getAsUser() == null || !oldGuide.getAsUser().equals(nowUserId)) {
            return new ResponseResult<>(403, "无权限操作");
        }

        LambdaUpdateWrapper<Guide> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Guide::getId, id);
        updateWrapper.eq(Guide::getIsDelete, 0);
        updateWrapper.eq(Guide::getAsUser, nowUserId);
        updateWrapper.set(Guide::getTitle, request == null || request.getTitle() == null ? oldGuide.getTitle() : request.getTitle().trim());
        updateWrapper.set(Guide::getContent, request == null ? oldGuide.getContent() : request.getContent());
        updateWrapper.set(Guide::getUpdateTime, now);
        updateWrapper.set(Guide::getUpdateBy, nowUserId);

        int updated = guideMapper.update(null, updateWrapper);
        if (updated <= 0) {
            return new ResponseResult<>(400, "更新失败");
        }

        LambdaQueryWrapper<GuideImg> imgWrapper = new LambdaQueryWrapper<>();
        imgWrapper.eq(GuideImg::getAsId, id);
        imgWrapper.eq(GuideImg::getIsDelete, 0);
        List<GuideImg> oldImgs = guideImgMapper.selectList(imgWrapper);
        List<Long> oldFileIds = oldImgs == null ? new ArrayList<>() : oldImgs.stream()
                .map(GuideImg::getFileId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (!oldFileIds.isEmpty()) {
            ResponseResult<Void> res = fileFeignClient.deleteFileLink(oldFileIds);
            if (res == null || res.getCode() == null || !res.getCode().equals(200)) {
                throw new BusinessException(500, "图片解绑失败");
            }
        }
        if (oldImgs != null && !oldImgs.isEmpty()) {
            guideImgMapper.delete(imgWrapper);
        }

        List<GuideImageDTO> images = normalizeImages(request);
        if (images != null && !images.isEmpty()) {
            List<Long> newFileIds = images.stream()
                    .map(GuideImageDTO::getFileId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();

            List<GuideImg> bindList = images.stream()
                    .filter(i -> i != null && i.getFileId() != null)
                    .map(i -> {
                        GuideImg img = new GuideImg();
                        img.setAsId(id);
                        img.setFileId(i.getFileId());
                        img.setSortIndex(i.getSortIndex() == null ? 0 : i.getSortIndex());
                        img.setCreateTime(now);
                        img.setUpdateTime(now);
                        img.setUpdateBy(nowUserId);
                        img.setIsDelete(0);
                        return img;
                    })
                    .toList();

            if (!bindList.isEmpty()) {
                guideImgMapper.insertBatchSomeColumn(bindList);
            }

            if (!newFileIds.isEmpty()) {
                ResponseResult<Void> addRes = fileFeignClient.addFileLink(newFileIds);
                if (addRes == null || addRes.getCode() == null || !addRes.getCode().equals(200)) {
                    throw new BusinessException(500, "图片绑定失败");
                }
            }
        }

        return new ResponseResult<>(200, "更新成功");
    }

    @Override
    @Transactional
    public ResponseResult<Void> deleteGuide(Long id) {
        if (id == null) {
            return new ResponseResult<>(400, "攻略不存在");
        }
        Long nowUserId = SystemCommonUtil.getNowUserId();

        LambdaQueryWrapper<Guide> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Guide::getId, id);
        wrapper.eq(Guide::getIsDelete, 0);
        Guide guide = guideMapper.selectOne(wrapper);
        if (guide == null) {
            return new ResponseResult<>(400, "攻略不存在");
        }
        if (guide.getAsUser() == null || !guide.getAsUser().equals(nowUserId)) {
            return new ResponseResult<>(403, "无权限操作");
        }

        LambdaQueryWrapper<GuideImg> imgWrapper = new LambdaQueryWrapper<>();
        imgWrapper.eq(GuideImg::getAsId, id);
        imgWrapper.eq(GuideImg::getIsDelete, 0);
        List<GuideImg> imgs = guideImgMapper.selectList(imgWrapper);
        List<Long> fileIds = imgs == null ? new ArrayList<>() : imgs.stream()
                .map(GuideImg::getFileId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (!fileIds.isEmpty()) {
            ResponseResult<Void> res = fileFeignClient.deleteFileLink(fileIds);
            if (res == null || res.getCode() == null || !res.getCode().equals(200)) {
                throw new BusinessException(500, "图片解绑失败");
            }
        }
        if (imgs != null && !imgs.isEmpty()) {
            guideImgMapper.delete(imgWrapper);
        }

        LambdaUpdateWrapper<Guide> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Guide::getId, id);
        updateWrapper.eq(Guide::getIsDelete, 0);
        updateWrapper.eq(Guide::getAsUser, nowUserId);
        updateWrapper.set(Guide::getIsDelete, 1);
        updateWrapper.set(Guide::getUpdateTime, System.currentTimeMillis());
        updateWrapper.set(Guide::getUpdateBy, nowUserId);
        guideMapper.update(null, updateWrapper);

        return new ResponseResult<>(200, "删除成功");
    }

    private ResponseResult<Map<String, Object>> buildGuideDetail(Long id, Long requiredOwnerId) {
        if (id == null) {
            return new ResponseResult<>(400, "攻略不存在");
        }

        LambdaQueryWrapper<Guide> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Guide::getId, id);
        wrapper.eq(Guide::getIsDelete, 0);
        Guide guide = guideMapper.selectOne(wrapper);
        if (guide == null) {
            return new ResponseResult<>(200, "查询成功", null);
        }
        if (requiredOwnerId != null) {
            if (guide.getAsUser() == null || !guide.getAsUser().equals(requiredOwnerId)) {
                return new ResponseResult<>(403, "无权限查看");
            }
        }

        LambdaQueryWrapper<GuideImg> imgWrapper = new LambdaQueryWrapper<>();
        imgWrapper.eq(GuideImg::getAsId, id);
        imgWrapper.eq(GuideImg::getIsDelete, 0);
        imgWrapper.orderByAsc(GuideImg::getSortIndex);
        imgWrapper.orderByAsc(GuideImg::getFileId);
        List<GuideImg> imgBinds = guideImgMapper.selectList(imgWrapper);

        List<Long> fileIds = imgBinds == null ? new ArrayList<>() : imgBinds.stream()
                .map(GuideImg::getFileId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<Long, SystemFile> fileMap = new HashMap<>();
        if (!fileIds.isEmpty()) {
            LambdaQueryWrapper<SystemFile> fileWrapper = new LambdaQueryWrapper<>();
            fileWrapper.in(SystemFile::getId, fileIds);
            fileWrapper.eq(SystemFile::getIsDelete, 0);
            List<SystemFile> files = systemFileMapper.selectList(fileWrapper);
            if (files != null) {
                for (SystemFile f : files) {
                    if (f == null || f.getId() == null) {
                        continue;
                    }
                    if (BaseTypeUtil.hasText(f.getPath())) {
                        f.setUrl(ossUtil.getTmpUrl(f.getPath()));
                    }
                    fileMap.put(f.getId(), f);
                }
            }
        }

        List<Map<String, Object>> images = new ArrayList<>();
        if (imgBinds != null) {
            for (GuideImg bind : imgBinds) {
                if (bind == null || bind.getFileId() == null) {
                    continue;
                }
                SystemFile f = fileMap.get(bind.getFileId());
                Map<String, Object> img = new HashMap<>();
                img.put("fileId", bind.getFileId());
                img.put("sortIndex", bind.getSortIndex() == null ? 0 : bind.getSortIndex());
                img.put("url", f == null ? "" : (f.getUrl() == null ? "" : f.getUrl()));
                images.add(img);
            }
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", guide.getId());
        Map<String, Object> asUser = new HashMap<>();
        asUser.put("id", guide.getAsUser() == null ? "" : String.valueOf(guide.getAsUser()));
        asUser.put("name", guide.getAsUser() == null ? "匿名" : ("用户" + guide.getAsUser()));
        asUser.put("avatarUrl", "");
        data.put("asUser", asUser);
        data.put("title", guide.getTitle() == null ? "" : guide.getTitle());
        data.put("content", guide.getContent() == null ? "" : guide.getContent());
        data.put("images", images);
        data.put("createTime", toIso(guide.getCreateTime()));
        data.put("updateTime", toIso(guide.getUpdateTime()));
        return new ResponseResult<>(200, "查询成功", data);
    }

    private Map<Long, String> buildGuideCoverUrlMap(List<Long> guideIds) {
        if (guideIds == null || guideIds.isEmpty()) {
            return new HashMap<>();
        }

        LambdaQueryWrapper<GuideImg> imgWrapper = new LambdaQueryWrapper<>();
        imgWrapper.in(GuideImg::getAsId, guideIds);
        imgWrapper.eq(GuideImg::getIsDelete, 0);
        imgWrapper.orderByAsc(GuideImg::getSortIndex);
        imgWrapper.orderByAsc(GuideImg::getFileId);
        List<GuideImg> binds = guideImgMapper.selectList(imgWrapper);
        if (binds == null || binds.isEmpty()) {
            return new HashMap<>();
        }

        Map<Long, Long> guideCoverFileIdMap = new HashMap<>();
        for (GuideImg bind : binds) {
            if (bind == null || bind.getAsId() == null || bind.getFileId() == null) {
                continue;
            }
            guideCoverFileIdMap.putIfAbsent(bind.getAsId(), bind.getFileId());
        }

        List<Long> fileIds = guideCoverFileIdMap.values().stream().filter(Objects::nonNull).distinct().toList();
        if (fileIds.isEmpty()) {
            return new HashMap<>();
        }

        LambdaQueryWrapper<SystemFile> fileWrapper = new LambdaQueryWrapper<>();
        fileWrapper.in(SystemFile::getId, fileIds);
        fileWrapper.eq(SystemFile::getIsDelete, 0);
        List<SystemFile> files = systemFileMapper.selectList(fileWrapper);
        Map<Long, String> fileIdUrlMap = new HashMap<>();
        if (files != null) {
            for (SystemFile f : files) {
                if (f == null || f.getId() == null || !BaseTypeUtil.hasText(f.getPath())) {
                    continue;
                }
                fileIdUrlMap.put(f.getId(), ossUtil.getTmpUrl(f.getPath()));
            }
        }

        Map<Long, String> coverUrlMap = new HashMap<>();
        for (Map.Entry<Long, Long> entry : guideCoverFileIdMap.entrySet()) {
            Long guideId = entry.getKey();
            Long fileId = entry.getValue();
            coverUrlMap.put(guideId, fileId == null ? "" : fileIdUrlMap.getOrDefault(fileId, ""));
        }
        return coverUrlMap;
    }

    private static List<GuideImageDTO> normalizeImages(GuideUpsertRequest request) {
        if (request == null) {
            return new ArrayList<>();
        }
        List<GuideImageDTO> images = request.getImages();
        if (images != null && !images.isEmpty()) {
            List<GuideImageDTO> list = images.stream()
                    .filter(i -> i != null && i.getFileId() != null)
                    .map(i -> new GuideImageDTO(i.getFileId(), i.getUrl(), i.getSortIndex()))
                    .toList();
            if (!list.isEmpty()) {
                return list;
            }
        }
        List<Long> ids = request.getImageFileIds();
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        List<GuideImageDTO> list = new ArrayList<>();
        int idx = 0;
        for (Long id : ids) {
            if (id == null) {
                continue;
            }
            list.add(new GuideImageDTO(id, "", idx++));
        }
        return list;
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
