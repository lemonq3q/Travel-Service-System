package org.example.feign;

import org.example.domain.encapsulate.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "service-file")
public interface FileFeignClient {

    @PostMapping("/file/link/add")
    public ResponseResult<Void> addFileLink(@RequestBody List<Long> ids);

    @PostMapping("/file/link/delete")
    public ResponseResult<Void> deleteFileLink(@RequestBody List<Long> ids);

}
