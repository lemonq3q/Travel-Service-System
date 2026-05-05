package org.example.controller;

import org.example.domain.encapsulate.ResponseResult;
import org.example.domain.encapsulate.ScrollPage;
import org.example.domain.encapsulate.TableData;
import org.example.domain.scenic.Scenic;
import org.example.domain.scenic.ScenicSelectDTO;
import org.example.service.ScenicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/scenic")
public class ScenicController {

    @Autowired
    private ScenicService scenicService;

    @GetMapping("/list")
    public ResponseResult<TableData<Scenic>> selectHotelList(ScenicSelectDTO scenicSelectDTO){
        return scenicService.selectScenicList(scenicSelectDTO);
    }

    @GetMapping("/{id}")
    public ResponseResult<Scenic> selectScenicById(@PathVariable("id") Long id){
        return scenicService.selectScenicById(id);
    }

    @GetMapping("/list/public")
    public ResponseResult<ScrollPage> searchScenics(
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "9") int limit,
            @RequestParam(required = false) String cityCode,
            @RequestParam(required = false) Integer needTicket,
            @RequestParam(required = false) Double ratingMin
    ) {
        return scenicService.publicSearchScenics(cursor, limit, cityCode, needTicket, ratingMin);
    }

    @GetMapping("/list/hot")
    public ResponseResult<Map<String, Object>> listHotScenics(@RequestParam(defaultValue = "6") int limit) {
        return scenicService.publicListHotScenics(limit);
    }

    @GetMapping("/{id}/public")
    public ResponseResult<Map<String, Object>> getScenicDetail(@PathVariable("id") Long id) {
        return scenicService.publicGetScenicDetail(id);
    }

    @PostMapping
    public ResponseResult<Void> insertScenic(@RequestBody Scenic scenic){
        return scenicService.insertScenic(scenic);
    }

    @PutMapping
    public ResponseResult<Void> updateScenic(@RequestBody Scenic scenic){
        return scenicService.updateScenic(scenic);
    }

    @DeleteMapping("/{id}")
    public ResponseResult<Void> deleteScenic(@PathVariable("id") Long id){
        return scenicService.deleteScenic(id);
    }

}
