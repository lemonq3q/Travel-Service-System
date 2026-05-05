package org.example.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.example.config.mybatisplus.BatchBaseMapper;
import org.example.domain.ImageBind;
import org.example.domain.SystemFile;
import org.example.domain.encapsulate.ResponseResult;
import org.example.feign.FileFeignClient;
import org.example.handler.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Component
public class WrapperUtil {

    @Autowired
    private FileFeignClient fileFeignClient;

    @Transactional
    public <T extends ImageBind> void updateImageBind(
            Long mainId,
            List<SystemFile> newSystemFiles,
            BatchBaseMapper<T> imgMapper,
            Supplier<T> entitySupplier
    ) {
        // 用new的话因为泛型会报错
        T entity = entitySupplier.get();
        LambdaQueryWrapper<T> wrapper = Wrappers.lambdaQuery(entity);

        wrapper.eq(T::getAsId, mainId);
        List<T> oldList = imgMapper.selectList(wrapper);

        if (!oldList.isEmpty()) {
            List<Long> oldFileIds = oldList.stream()
                    .map(T::getFileId)
                    .collect(Collectors.toList());

            ResponseResult res = fileFeignClient.deleteFileLink(oldFileIds);
            if (!res.getCode().equals(200)) {
                throw new BusinessException(400, "删除图片绑定失败");
            }

            imgMapper.delete(wrapper);
        }

        if (newSystemFiles == null || newSystemFiles.isEmpty()) {
            return;
        }

        List<Long> newFileIds = newSystemFiles.stream()
                .map(SystemFile::getId)
                .collect(Collectors.toList());

        List<T> insertList = newFileIds.stream().map(fileId -> {
            T t = entitySupplier.get();
            t.setAsId(mainId);
            t.setFileId(fileId);
            t.setIsDelete(0);
            return t;
        }).collect(Collectors.toList());

        imgMapper.insertBatchSomeColumn(insertList);

        ResponseResult addRes = fileFeignClient.addFileLink(newFileIds);
        if (!addRes.getCode().equals(200)) {
            throw new BusinessException(400, "添加图片绑定失败");
        }
    }

    @Transactional
    public <T extends ImageBind> void updateImageBind(
            Map<Long, List<SystemFile>> newSystemFileMap,
            BatchBaseMapper<T> imgMapper,
            Supplier<T> entitySupplier
    ){
        List<Long> mainIds = newSystemFileMap.keySet().stream().toList();
        if (mainIds.isEmpty()) {
            return;
        }

        // 用new的话因为泛型会报错
        T entity = entitySupplier.get();
        LambdaQueryWrapper<T> wrapper = Wrappers.lambdaQuery(entity);

        wrapper.in(T::getAsId, mainIds);
        List<T> oldList = imgMapper.selectList(wrapper);

        if (!oldList.isEmpty()) {
            List<Long> oldFileIds = oldList.stream()
                    .map(T::getFileId)
                    .collect(Collectors.toList());

            ResponseResult res = fileFeignClient.deleteFileLink(oldFileIds);
            if (!res.getCode().equals(200)) {
                throw new BusinessException(400, "删除图片绑定失败");
            }

            imgMapper.delete(wrapper);
        }

        List<T> insertList = new ArrayList<>();
        for (Map.Entry<Long, List<SystemFile>> entry : newSystemFileMap.entrySet()) {
            Long mainId = entry.getKey();
            List<SystemFile> newSystemFiles = entry.getValue();
            if (newSystemFiles == null || newSystemFiles.isEmpty()){
                continue;
            }
            List<Long> newFileIds = newSystemFiles.stream()
                    .map(SystemFile::getId)
                    .toList();
            List<T> tmpList = newFileIds.stream().map(fileId -> {
                T t = entitySupplier.get();
                t.setAsId(mainId);
                t.setFileId(fileId);
                t.setIsDelete(0);
                return t;
            }).toList();
            insertList.addAll(tmpList);
        }

        if (insertList.isEmpty()) {
            return;
        }

        imgMapper.insertBatchSomeColumn(insertList);

        List<Long> newFileIds = insertList.stream()
                .map(T::getFileId)
                .toList();

        ResponseResult addRes = fileFeignClient.addFileLink(newFileIds);
        if (!addRes.getCode().equals(200)) {
            throw new BusinessException(400, "添加图片绑定失败");
        }
    }
}