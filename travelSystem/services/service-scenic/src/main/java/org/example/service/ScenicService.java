package org.example.service;

import org.example.domain.encapsulate.ResponseResult;
import org.example.domain.encapsulate.ScrollPage;
import org.example.domain.encapsulate.TableData;
import org.example.domain.scenic.Scenic;
import org.example.domain.scenic.ScenicSelectDTO;

import java.util.Map;

public interface ScenicService {
    ResponseResult<TableData<Scenic>> selectScenicList(ScenicSelectDTO scenicSelectDTO);

    ResponseResult<Scenic> selectScenicById(Long id);

    ResponseResult<ScrollPage> publicSearchScenics(String cursor, int limit, String cityCode, Integer needTicket, Double ratingMin);

    ResponseResult<Map<String, Object>> publicListHotScenics(int limit);

    ResponseResult<Map<String, Object>> publicGetScenicDetail(Long id);

    ResponseResult<Void> insertScenic(Scenic scenic);

    ResponseResult<Void> updateScenic(Scenic scenic);

    ResponseResult<Void> deleteScenic(Long id);
}
