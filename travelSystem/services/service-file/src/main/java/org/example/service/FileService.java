package org.example.service;

import org.example.domain.SystemFile;
import org.example.domain.encapsulate.ResponseResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    ResponseResult<SystemFile> uploadFile(MultipartFile file);

    ResponseResult<Void> addFileLink(List<Long> ids);

    ResponseResult<Void> deleteFileLink(List<Long> ids);
}
