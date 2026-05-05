package org.example.controller;

import org.example.domain.encapsulate.ResponseResult;
import org.example.domain.encapsulate.ScrollPage;
import org.example.domain.guide.dto.GuideUpsertRequest;
import org.example.service.GuideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/guide")
public class GuideController {

    @Autowired
    private GuideService guideService;

    @GetMapping("/list/public")
    public ResponseResult<ScrollPage> publicSearchGuides(
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "9") int limit,
            @RequestParam(required = false) String keyword
    ) {
        return guideService.publicSearchGuides(cursor, limit, keyword);
    }

    @GetMapping("/{id}/public")
    public ResponseResult<Map<String, Object>> publicGetGuideDetail(@PathVariable("id") Long id) {
        return guideService.publicGetGuideDetail(id);
    }

    @GetMapping("/my/list")
    public ResponseResult<ScrollPage> listMyGuides(
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return guideService.listMyGuides(cursor, limit);
    }

    @GetMapping("/my/{id}")
    public ResponseResult<Map<String, Object>> getMyGuideDetail(@PathVariable("id") Long id) {
        return guideService.getMyGuideDetail(id);
    }

    @PostMapping("/my")
    public ResponseResult<Map<String, Object>> createGuide(@RequestBody GuideUpsertRequest request) {
        return guideService.createGuide(request);
    }

    @PostMapping
    public ResponseResult<Map<String, Object>> createGuideCompat(@RequestBody GuideUpsertRequest request) {
        return guideService.createGuide(request);
    }

    @PutMapping("/my/{id}")
    public ResponseResult<Void> updateGuide(@PathVariable("id") Long id, @RequestBody GuideUpsertRequest request) {
        return guideService.updateGuide(id, request);
    }

    @DeleteMapping("/my/{id}")
    public ResponseResult<Void> deleteGuide(@PathVariable("id") Long id) {
        return guideService.deleteGuide(id);
    }
}
