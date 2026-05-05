package org.example.service;

import org.example.domain.encapsulate.ResponseResult;
import org.example.domain.encapsulate.ScrollPage;
import org.example.domain.guide.dto.GuideUpsertRequest;

import java.util.Map;

public interface GuideService {

    ResponseResult<ScrollPage> publicSearchGuides(String cursor, int limit, String keyword);

    ResponseResult<Map<String, Object>> publicGetGuideDetail(Long id);

    ResponseResult<ScrollPage> listMyGuides(String cursor, int limit);

    ResponseResult<Map<String, Object>> getMyGuideDetail(Long id);

    ResponseResult<Map<String, Object>> createGuide(GuideUpsertRequest request);

    ResponseResult<Void> updateGuide(Long id, GuideUpsertRequest request);

    ResponseResult<Void> deleteGuide(Long id);
}
