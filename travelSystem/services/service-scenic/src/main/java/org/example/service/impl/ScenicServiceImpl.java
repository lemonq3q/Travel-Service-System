package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageHelper;
import org.example.domain.SystemFile;
import org.example.domain.encapsulate.ResponseResult;
import org.example.domain.encapsulate.ScrollPage;
import org.example.domain.encapsulate.TableData;
import org.example.domain.scenic.Scenic;
import org.example.domain.scenic.ScenicImg;
import org.example.domain.scenic.ScenicSelectDTO;
import org.example.feign.FileFeignClient;
import org.example.handler.exception.BusinessException;
import org.example.mapper.ScenicImgMapper;
import org.example.mapper.ScenicMapper;
import org.example.mapper.SystemFileMapper;
import org.example.service.ScenicService;
import org.example.utils.BaseTypeUtil;
import org.example.utils.OSSUtil;
import org.example.utils.SystemCommonUtil;
import org.example.utils.WrapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ScenicServiceImpl implements ScenicService {

    @Autowired
    private ScenicMapper scenicMapper;
    @Autowired
    private ScenicImgMapper scenicImgMapper;

    @Autowired
    private SystemFileMapper systemFileMapper;

    @Autowired
    private FileFeignClient fileFeignClient;

    @Autowired
    private WrapperUtil wrapperUtil;

    @Autowired
    private OSSUtil ossUtil;

    @Override
    public ResponseResult<TableData<Scenic>> selectScenicList(ScenicSelectDTO scenicSelectDTO) {
        PageHelper.startPage(scenicSelectDTO.getPageNum(),scenicSelectDTO.getPageSize());
        LambdaQueryWrapper<Scenic> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Scenic::getIsDelete, 0);
        if(BaseTypeUtil.hasText(scenicSelectDTO.getName())){
            wrapper.like(Scenic::getName,scenicSelectDTO.getName());
        }
        if(BaseTypeUtil.hasText(scenicSelectDTO.getAreaCode())){
            wrapper.eq(Scenic::getAreaCode,scenicSelectDTO.getAreaCode());
        }
        if(scenicSelectDTO.getNeedTicket() != null){
            wrapper.eq(Scenic::getNeedTicket,scenicSelectDTO.getNeedTicket());
        }
        List<Scenic> scenicList = scenicMapper.selectList(wrapper);
        TableData<Scenic> data = new TableData<>(scenicList);
        return new ResponseResult<>(200, "查询成功", data);
    }

    @Override
    public ResponseResult<Scenic> selectScenicById(Long id) {
        Scenic scenic = scenicMapper.selectScenicById(id);
        if(BaseTypeUtil.listHasItem(scenic.getScenicImgList())){
            scenic.getScenicImgList().forEach(img -> ossUtil.buildTmpUrl(img));
        }
        return new ResponseResult<>(200, "查询成功", scenic);
    }

    @Override
    public ResponseResult<ScrollPage> publicSearchScenics(String cursor, int limit, String cityCode, Integer needTicket, Double ratingMin) {
        int pageSize = limit <= 0 ? 9 : Math.min(limit, 50);

        Long cursorId = decodeCursorId(cursor);
        LambdaQueryWrapper<Scenic> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Scenic::getIsDelete, 0);
        if (BaseTypeUtil.hasText(cityCode)) {
            wrapper.eq(Scenic::getAreaCode, cityCode);
        }
        if (needTicket != null) {
            wrapper.eq(Scenic::getNeedTicket, needTicket);
        }
        if (ratingMin != null) {
            wrapper.ge(Scenic::getRating, BigDecimal.valueOf(ratingMin));
        }
        if (cursorId != null) {
            wrapper.lt(Scenic::getId, cursorId);
        }
        wrapper.orderByDesc(Scenic::getId);
        wrapper.last("LIMIT " + pageSize);
        List<Scenic> scenicList = scenicMapper.selectList(wrapper);
        if (scenicList == null || scenicList.isEmpty()) {
            return new ResponseResult<>(200, "查询成功", new ScrollPage<>(new ArrayList<>(), null));
        }

        List<Long> scenicIds = scenicList.stream().map(Scenic::getId).filter(Objects::nonNull).toList();
        Map<Long, List<String>> scenicImagesMap = buildScenicImageUrlMap(scenicIds);

        List<Map<String, Object>> items = scenicList.stream().map(s -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", s.getId());
            m.put("name", s.getName());
            m.put("address", s.getAddress());
            m.put("areaCode", s.getAreaCode());
            m.put("longitude", s.getLongitude());
            m.put("latitude", s.getLatitude());
            m.put("rating", s.getRating() == null ? 0 : s.getRating());
            m.put("hotValue", s.getHotValue() == null ? 0 : s.getHotValue());
            m.put("needTicket", s.getNeedTicket() == null ? 0 : s.getNeedTicket());
            m.put("price", s.getPrice() == null ? 0 : s.getPrice());
            List<String> images = scenicImagesMap.getOrDefault(s.getId(), new ArrayList<>());
            m.put("images", images);
            m.put("coverImageUrl", images.isEmpty() ? "" : images.get(0));
            return m;
        }).toList();

        String nextCursor = null;
        if (scenicList.size() == pageSize) {
            Scenic last = scenicList.get(scenicList.size() - 1);
            nextCursor = encodeCursorId(last.getId());
        }

        return new ResponseResult<>(200, "查询成功", new ScrollPage<>(items, nextCursor));
    }

    @Override
    public ResponseResult<Map<String, Object>> publicListHotScenics(int limit) {
        int pageSize = limit <= 0 ? 6 : Math.min(limit, 50);

        LambdaQueryWrapper<Scenic> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Scenic::getIsDelete, 0);
        wrapper.orderByDesc(Scenic::getHotValue);
        wrapper.orderByDesc(Scenic::getId);
        wrapper.last("LIMIT " + pageSize);
        List<Scenic> scenicList = scenicMapper.selectList(wrapper);

        List<Long> scenicIds = scenicList == null ? new ArrayList<>() : scenicList.stream().map(Scenic::getId).filter(Objects::nonNull).toList();
        Map<Long, List<String>> scenicImagesMap = buildScenicImageUrlMap(scenicIds);

        List<Map<String, Object>> items = new ArrayList<>();
        if (scenicList != null) {
            for (Scenic s : scenicList) {
                Map<String, Object> m = new HashMap<>();
                m.put("id", s.getId());
                m.put("name", s.getName());
                m.put("address", s.getAddress());
                m.put("areaCode", s.getAreaCode());
                m.put("longitude", s.getLongitude());
                m.put("latitude", s.getLatitude());
                m.put("rating", s.getRating() == null ? 0 : s.getRating());
                m.put("hotValue", s.getHotValue() == null ? 0 : s.getHotValue());
                m.put("needTicket", s.getNeedTicket() == null ? 0 : s.getNeedTicket());
                m.put("price", s.getPrice() == null ? 0 : s.getPrice());
                List<String> images = scenicImagesMap.getOrDefault(s.getId(), new ArrayList<>());
                m.put("images", images);
                m.put("coverImageUrl", images.isEmpty() ? "" : images.get(0));
                items.add(m);
            }
        }

        Map<String, Object> data = new HashMap<>();
        data.put("items", items);
        return new ResponseResult<>(200, "查询成功", data);
    }

    @Override
    public ResponseResult<Map<String, Object>> publicGetScenicDetail(Long id) {
        ResponseResult<Scenic> res = selectScenicById(id);
        if (res == null || res.getCode() == null || !res.getCode().equals(200) || res.getData() == null) {
            return new ResponseResult<>(400, res == null ? "景区不存在" : (res.getMsg() == null ? "景区不存在" : res.getMsg()));
        }

        Scenic s = res.getData();
        Map<String, Object> m = new HashMap<>();
        m.put("id", s.getId());
        m.put("name", s.getName());
        m.put("address", s.getAddress());
        m.put("areaCode", s.getAreaCode());
        m.put("longitude", s.getLongitude());
        m.put("latitude", s.getLatitude());
        m.put("desc", s.getScenicDesc() == null ? "" : s.getScenicDesc());
        m.put("openTimeDesc", s.getOpenTimeDesc() == null ? "" : s.getOpenTimeDesc());
        m.put("rating", s.getRating() == null ? 0 : s.getRating());
        m.put("hotValue", s.getHotValue() == null ? 0 : s.getHotValue());
        m.put("needTicket", s.getNeedTicket() == null ? 0 : s.getNeedTicket());
        m.put("price", s.getPrice() == null ? 0 : s.getPrice());

        List<String> images = new ArrayList<>();
        if (s.getScenicImgList() != null) {
            for (SystemFile img : s.getScenicImgList()) {
                if (img != null && BaseTypeUtil.hasText(img.getUrl())) {
                    images.add(img.getUrl());
                }
            }
        }
        m.put("images", images);

        return new ResponseResult<>(200, "查询成功", m);
    }

    @Override
    @Transactional
    public ResponseResult<Void> insertScenic(Scenic scenic) {
        Long nowUserId = SystemCommonUtil.getNowUserId();

        scenic.setUpdateBy(nowUserId);
        scenicMapper.insert(scenic);

        if(BaseTypeUtil.listHasItem(scenic.getScenicImgList())){
            // 插入绑定数据
            List<Long> imgIds = scenic.getScenicImgList()
                    .stream().map(SystemFile::getId).toList();
            List<ScenicImg> scenicImgList = imgIds.stream().map(imgId -> {
                ScenicImg scenicImg = new ScenicImg();
                scenicImg.setAsId(scenic.getId());
                scenicImg.setFileId(imgId);
                scenicImg.setIsDelete(0);
                scenicImg.setUpdateBy(nowUserId);
                return scenicImg;
            }).toList();
            scenicImgMapper.insertBatchSomeColumn(scenicImgList);

            // 更新link状态
            ResponseResult res = fileFeignClient.addFileLink(imgIds);
            if(res.getCode() != 200){
                throw new BusinessException(500, "图片绑定失败");
            }
        }

        return new ResponseResult<>(200, "添加成功");
    }

    @Override
    @Transactional
    public ResponseResult<Void> updateScenic(Scenic scenic) {
        Long nowUserId = SystemCommonUtil.getNowUserId();

        LambdaUpdateWrapper<Scenic> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Scenic::getId, scenic.getId());
        wrapper.eq(Scenic::getIsDelete, 0);
        wrapper.set(Scenic::getUpdateBy, nowUserId);

        int update = scenicMapper.update(scenic, wrapper);
        if (update <= 0){
            return new ResponseResult<>(400, "景区不存在");
        }

        // 更新图片绑定
        wrapperUtil.updateImageBind(scenic.getId(), scenic.getScenicImgList(), scenicImgMapper, ScenicImg::new);

        return new ResponseResult<>(200, "更新成功");

    }

    @Override
    @Transactional
    public ResponseResult<Void> deleteScenic(Long id) {
        Long nowUserId = SystemCommonUtil.getNowUserId();
        Scenic scenic = scenicMapper.selectScenicById(id);
        if(scenic == null){
            return new ResponseResult<>(400, "景区不存在");
        }
        // 删除图片
        if(BaseTypeUtil.listHasItem(scenic.getScenicImgList())){
            // 删除link
            List<Long> deleteImgIds = scenic.getScenicImgList()
                    .stream().map(SystemFile::getId).toList();
            ResponseResult res = fileFeignClient.deleteFileLink(deleteImgIds);
            if(res.getCode() != 200){
                throw new BusinessException(500, "图片解绑失败");
            }
            // 删除绑定
            LambdaQueryWrapper<ScenicImg> imgWrapper = new LambdaQueryWrapper<>();
            imgWrapper.eq(ScenicImg::getAsId, id);
            scenicImgMapper.delete(imgWrapper);
        }
        // 删除本体
        LambdaUpdateWrapper<Scenic> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Scenic::getId, id);
        wrapper.eq(Scenic::getIsDelete, 0);
        wrapper.set(Scenic::getIsDelete, 1);
        wrapper.set(Scenic::getUpdateBy, nowUserId);
        scenicMapper.update(null, wrapper);

        return new ResponseResult<>(200, "删除成功");
    }

    private Map<Long, List<String>> buildScenicImageUrlMap(List<Long> scenicIds) {
        if (scenicIds == null || scenicIds.isEmpty()) {
            return new HashMap<>();
        }

        LambdaQueryWrapper<ScenicImg> imgWrapper = new LambdaQueryWrapper<>();
        imgWrapper.in(ScenicImg::getAsId, scenicIds);
        imgWrapper.eq(ScenicImg::getIsDelete, 0);
        List<ScenicImg> imgBinds = scenicImgMapper.selectList(imgWrapper);

        Map<Long, List<Long>> scenicFileIdsMap = new HashMap<>();
        List<Long> fileIds = new ArrayList<>();
        if (imgBinds != null) {
            for (ScenicImg bind : imgBinds) {
                if (bind == null || bind.getAsId() == null || bind.getFileId() == null) {
                    continue;
                }
                scenicFileIdsMap.computeIfAbsent(bind.getAsId(), k -> new ArrayList<>()).add(bind.getFileId());
                fileIds.add(bind.getFileId());
            }
        }

        if (fileIds.isEmpty()) {
            return new HashMap<>();
        }

        LambdaQueryWrapper<SystemFile> fileWrapper = new LambdaQueryWrapper<>();
        fileWrapper.in(SystemFile::getId, fileIds.stream().distinct().toList());
        fileWrapper.eq(SystemFile::getIsDelete, 0);
        List<SystemFile> files = systemFileMapper.selectList(fileWrapper);
        Map<Long, String> fileIdPathMap = files.stream()
                .filter(f -> f != null && f.getId() != null && BaseTypeUtil.hasText(f.getPath()))
                .collect(Collectors.toMap(SystemFile::getId, SystemFile::getPath, (a, b) -> a));

        Map<Long, List<String>> scenicImagesMap = new HashMap<>();
        for (Map.Entry<Long, List<Long>> entry : scenicFileIdsMap.entrySet()) {
            Long scenicId = entry.getKey();
            List<Long> ids = entry.getValue();
            if (ids == null || ids.isEmpty()) {
                continue;
            }
            List<String> urls = new ArrayList<>();
            for (Long fileId : ids) {
                String path = fileIdPathMap.get(fileId);
                if (BaseTypeUtil.hasText(path)) {
                    urls.add(ossUtil.getTmpUrl(path));
                }
            }
            scenicImagesMap.put(scenicId, urls);
        }

        return scenicImagesMap;
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
