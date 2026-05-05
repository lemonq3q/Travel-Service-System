package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.example.domain.SystemFile;
import org.example.domain.encapsulate.ResponseResult;
import org.example.mapper.SystemFileMapper;
import org.example.service.FileService;
import org.example.utils.OSSUtil;
import org.example.utils.SystemCommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private SystemFileMapper systemFileMapper;

    @Autowired
    private OSSUtil ossUtil;
    @Autowired
    private ConversionService conversionService;

    @Override
    public ResponseResult<SystemFile> uploadFile(MultipartFile file) {
        String fileName = UUID.randomUUID() + file.getOriginalFilename();
        if(fileName.length() > 200){
            fileName = fileName.substring(0, 200);
        }
        if (OSSUtil.uploadFile(file, fileName)) {
            SystemFile systemFile = new SystemFile();
            systemFile.setId(null);
            systemFile.setFileName(file.getOriginalFilename());
            systemFile.setPath(fileName);
            systemFile.setUpdateBy(SystemCommonUtil.getNowUserId());
            systemFileMapper.insert(systemFile);

            ossUtil.buildTmpUrl(systemFile);
            System.out.println(systemFile);
            return new ResponseResult(200, systemFile);
        } else {
            return new ResponseResult(500, "上传失败");
        }
    }

    @Override
    public ResponseResult<Void> addFileLink(List<Long> ids) {
        LambdaUpdateWrapper<SystemFile> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(SystemFile::getId, ids);
        wrapper.set(SystemFile::getIsLinked, 1);
        systemFileMapper.update(null, wrapper);
        return new ResponseResult(200, "操作成功");
    }

    @Override
    public ResponseResult<Void> deleteFileLink(List<Long> ids) {
        LambdaUpdateWrapper<SystemFile> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(SystemFile::getId, ids);
        wrapper.set(SystemFile::getIsLinked, 0);
        systemFileMapper.update(null, wrapper);
        return new ResponseResult(200, "操作成功");
    }

}
