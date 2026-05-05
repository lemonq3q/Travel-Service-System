package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.domain.encapsulate.ResponseResult;
import org.example.domain.encapsulate.ScrollPage;
import org.example.domain.encapsulate.TableData;
import org.example.domain.hotel.HotelRoom;
import org.example.domain.hotel.RoomTypePrice;
import org.example.domain.hotel.Hotel;
import org.example.domain.hotel.HotelFilter;
import org.example.domain.hotel.HotelSelectDTO;
import org.example.utils.BaseTypeUtil;
import org.example.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/hotel")
public class HotelController {

    @Autowired
    private HotelService hotelService;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/list")
    public ResponseResult<TableData<Hotel>> selectHotelList(HotelSelectDTO hotelSelectDTO){
        return hotelService.selectHotelList(hotelSelectDTO);
    }

    @GetMapping("/public/list")
    public ResponseResult<ScrollPage> publicSearchHotels(
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "recommended") String sort,
            @RequestParam(required = false) String filters  // 前端传的是 JSON 字符串
    ) throws Exception {
        HotelFilter filter = null;
        if (filters != null) {
            filter = objectMapper.readValue(filters, HotelFilter.class);
        }

        return hotelService.publicSearchHotels(cursor, limit, sort, filter);
    }

    @GetMapping("/{id}")
    public ResponseResult<Hotel> selectHotelById(@PathVariable("id") Long id){
        return hotelService.selectHotelById(id);
    }

    @GetMapping("/public/{id}")
    public ResponseResult<Map<String, Object>> getHotelBundle(@PathVariable("id") Long id) {
        ResponseResult<Hotel> res = hotelService.selectHotelById(id);
        if (res == null || res.getCode() == null || !res.getCode().equals(200) || res.getData() == null) {
            return new ResponseResult<>(400, res == null ? "酒店不存在" : (res.getMsg() == null ? "酒店不存在" : res.getMsg()));
        }

        Hotel h = res.getData();
        Map<String, Object> hotel = new HashMap<>();
        hotel.put("id", String.valueOf(h.getId()));
        hotel.put("name", h.getName());
        hotel.put("address", h.getAddress());
        hotel.put("cityCode", h.getAreaCode());
        hotel.put("longitude", h.getLongitude());
        hotel.put("latitude", h.getLatitude());
        hotel.put("starLevel", h.getStarLevel() == null ? 0 : h.getStarLevel());
        hotel.put("rating", h.getRating() == null ? 0 : h.getRating());
        hotel.put("reviewCount", 0);
        hotel.put("desc", h.getHotelDesc() == null ? "" : h.getHotelDesc());
        hotel.put("featureJson", h.getFeatureJson());
        hotel.put("facilityJson", h.getFacilityJson());
        hotel.put("serviceJson", h.getServiceJson());

        List<String> images = new ArrayList<>();
        if (h.getHotelImgList() != null) {
            h.getHotelImgList().forEach(img -> {
                if (img != null && BaseTypeUtil.hasText(img.getUrl())) {
                    images.add(img.getUrl());
                }
            });
        }
        hotel.put("images", images);

        List<Map<String, Object>> rooms = new ArrayList<>();
        if (h.getHotelRoomList() != null) {
            for (HotelRoom r : h.getHotelRoomList()) {
                if (r == null) {
                    continue;
                }
                Map<String, Object> room = new HashMap<>();
                room.put("id", String.valueOf(r.getId()));
                room.put("hotelId", String.valueOf(r.getAsHotel()));
                room.put("name", r.getName());
                room.put("maxPeople", r.getMaxPeople() == null ? 0 : r.getMaxPeople());
                room.put("desc", r.getRoomDesc() == null ? "" : r.getRoomDesc());
                room.put("featureJson", r.getFeatureJson());

                List<String> roomImages = new ArrayList<>();
                if (r.getRoomImgList() != null) {
                    r.getRoomImgList().forEach(img -> {
                        if (img != null && BaseTypeUtil.hasText(img.getUrl())) {
                            roomImages.add(img.getUrl());
                        }
                    });
                }
                room.put("images", roomImages);

                List<Map<String, Object>> pkgs = new ArrayList<>();
                if (r.getRoomTypePriceList() != null) {
                    for (RoomTypePrice p : r.getRoomTypePriceList()) {
                        if (p == null) {
                            continue;
                        }
                        Map<String, Object> pkg = new HashMap<>();
                        pkg.put("id", String.valueOf(p.getId()));
                        pkg.put("summaryJson", p.getSummaryJson());
                        Map<String, Object> money = new HashMap<>();
                        money.put("currency", "CNY");
                        money.put("amount", p.getPrice() == null ? 0 : p.getPrice());
                        pkg.put("price", money);
                        pkgs.add(pkg);
                    }
                }
                room.put("roomTypePriceList", pkgs);

                rooms.add(room);
            }
        }

        Map<String, Object> bundle = new HashMap<>();
        bundle.put("hotel", hotel);
        bundle.put("rooms", rooms);

        return new ResponseResult<>(200, "查询成功", bundle);
    }

    @PostMapping
    public ResponseResult<Void> insertHotel(@RequestBody Hotel hotel){
        return hotelService.insertHotel(hotel);
    }

    @PutMapping
    public ResponseResult<Void> updateHotel(@RequestBody Hotel hotel){
        return hotelService.updateHotel(hotel);
    }

    @DeleteMapping("/{id}")
    public ResponseResult<Void> deleteHotel(@PathVariable("id") Long id){
        return hotelService.deleteHotel(id);
    }

}
