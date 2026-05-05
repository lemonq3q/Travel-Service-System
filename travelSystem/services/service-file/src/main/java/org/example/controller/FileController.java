package org.example.controller;

import org.example.domain.SystemFile;
import org.example.domain.encapsulate.ResponseResult;
import org.example.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping
    public ResponseResult<SystemFile> uploadFile(@RequestParam("file") MultipartFile file) {
        return fileService.uploadFile(file);
    }

    @PostMapping("/link/add")
    public ResponseResult<Void> addFileLink(@RequestBody List<Long> ids) {
        return fileService.addFileLink(ids);
    }

    @PostMapping("/link/delete")
    public ResponseResult<Void> deleteFileLink(@RequestBody List<Long> ids) {
        return fileService.deleteFileLink(ids);
    }
}
