package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageHelper;
import org.example.domain.SystemFile;
import org.example.domain.encapsulate.ResponseResult;
import org.example.domain.encapsulate.ScrollPage;
import org.example.domain.encapsulate.TableData;
import org.example.domain.hotel.*;
import org.example.feign.FileFeignClient;
import org.example.handler.exception.BusinessException;
import org.example.mapper.*;
import org.example.service.HotelService;
import org.example.utils.BaseTypeUtil;
import org.example.utils.OSSUtil;
import org.example.utils.SystemCommonUtil;
import org.example.utils.WrapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class HotelServiceImpl implements HotelService {

    @Autowired
    private HotelMapper hotelMapper;
    @Autowired
    private HotelRoomMapper hotelRoomMapper;
    @Autowired
    private HotelImgMapper hotelImgMapper;
    @Autowired
    private RoomImgMapper roomImgMapper;
    @Autowired
    private RoomTypePriceMapper roomTypePriceMapper;
    @Autowired
    private SystemFileMapper systemFileMapper;

    @Autowired
    private FileFeignClient fileFeignClient;

    @Autowired
    private OSSUtil ossUtil;

    @Autowired
    private WrapperUtil wrapperUtil;

    @Override
    public ResponseResult<TableData<Hotel>> selectHotelList(HotelSelectDTO hotelSelectDTO) {
        PageHelper.startPage(hotelSelectDTO.getPageNum(), hotelSelectDTO.getPageSize());

        LambdaQueryWrapper<Hotel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Hotel::getIsDelete, 0);

        if(BaseTypeUtil.hasText(hotelSelectDTO.getName())) {
            wrapper.like(Hotel::getName, hotelSelectDTO.getName());
        }
        if(BaseTypeUtil.hasText(hotelSelectDTO.getAreaCode())) {
            wrapper.eq(Hotel::getAreaCode, hotelSelectDTO.getAreaCode());
        }
        if(hotelSelectDTO.getStarLevel() != null) {
            wrapper.eq(Hotel::getStarLevel, hotelSelectDTO.getStarLevel());
        }

        List<Hotel> hotels = hotelMapper.selectList(wrapper);
        TableData<Hotel> tableData = new TableData<>(hotels);

        return new ResponseResult<>(200, "查询成功", tableData);
    }

    @Override
    public ResponseResult<Hotel> selectHotelById(Long id) {
        Hotel hotel = hotelMapper.selectHotelById(id);
        if(hotel == null){
            return new ResponseResult<>(400, "酒店不存在");
        }
        // 构造图片临时url
        if(hotel.getHotelImgList() != null){
            hotel.getHotelImgList().forEach(hotelImg -> ossUtil.buildTmpUrl(hotelImg));
        }
        if(hotel.getHotelRoomList() != null){
            hotel.getHotelRoomList().forEach(hotelRoom -> {
                if(hotelRoom.getRoomImgList() != null){
                    hotelRoom.getRoomImgList().forEach(hotelRoomImg -> ossUtil.buildTmpUrl(hotelRoomImg));
                }
            });
        }
        return new ResponseResult<>(200, "查询成功", hotel);
    }

    @Override
    @Transactional
    public ResponseResult<Void> updateHotel(Hotel hotel) {
        long nowUserId = SystemCommonUtil.getNowUserId();

        hotel.setUpdateBy(nowUserId);
        LambdaUpdateWrapper<Hotel> hotelWrapper = new LambdaUpdateWrapper<>();
        hotelWrapper.eq(Hotel::getId, hotel.getId());
        hotelWrapper.eq(Hotel::getIsDelete, 0);
        int x = hotelMapper.update(hotel, hotelWrapper);
        if(x <= 0){
            return new ResponseResult<>(400, "酒店不存在");
        }
        if(hotel.getHotelImgList() != null){
            wrapperUtil.updateImageBind(hotel.getId(), hotel.getHotelImgList(), hotelImgMapper, HotelImg::new);
        }
        if(hotel.getHotelRoomList() != null){
            // 需要更新的房间
            List<HotelRoom> hasIdRoomList = hotel.getHotelRoomList().stream()
                    .filter(room -> room.getId() != null)
                    .toList();
            // 需要新增的房间
            List<HotelRoom> noIdRoomList = hotel.getHotelRoomList().stream()
                    .filter(room -> room.getId() == null)
                    .toList();
            if(!hasIdRoomList.isEmpty()){
                // 需要更新的房间id
                List<Long> hasIdRoomIds = hasIdRoomList.stream()
                        .map(HotelRoom::getId)
                        .toList();
                // 查询存在的房间id
                LambdaQueryWrapper<HotelRoom> roomQueryWrapper = new LambdaQueryWrapper<>();
                roomQueryWrapper.eq(HotelRoom::getAsHotel, hotel.getId());
                roomQueryWrapper.eq(HotelRoom::getIsDelete, 0);
                List<Long> existRoomIds = hotelRoomMapper.selectList(roomQueryWrapper).stream()
                        .map(HotelRoom::getId)
                        .toList();
                // 查询需要删除的房间id并删除
                List<Long> deleteRoomIds = existRoomIds.stream()
                        .filter(roomId -> !hasIdRoomIds.contains(roomId))
                        .toList();
                if(!deleteRoomIds.isEmpty()){
                    deleteHotelRoomByIds(deleteRoomIds);
                }
                // 更新房间
                updateHotelRoomList(hasIdRoomList);
            }
            // 新增房间
            if(!noIdRoomList.isEmpty()){
                addHotelRoomList(noIdRoomList, hotel.getId());
            }
        }
        return new ResponseResult<>(200, "更新成功");
    }

    @Override
    @Transactional
    public ResponseResult<Void> insertHotel(Hotel hotel) {
        long nowUserId = SystemCommonUtil.getNowUserId();

        // 插入酒店
        hotel.setUpdateBy(nowUserId);
        hotelMapper.insert(hotel);

        Long hotelId = hotel.getId();
        // 插入酒店绑定的图片
        if(hotel.getHotelImgList() != null && !hotel.getHotelImgList().isEmpty()){
            List<HotelImg> hotelImgList = hotel.getHotelImgList().stream()
                    .map(img -> {
                        HotelImg hotelImg = new HotelImg();
                        hotelImg.setAsId(hotelId);
                        hotelImg.setFileId(img.getId());
                        hotelImg.setIsDelete(0);
                        hotelImg.setUpdateBy(nowUserId);
                        return hotelImg;
                    }).toList();
            hotelImgMapper.insertBatchSomeColumn(hotelImgList);

            List<Long> newFileIds = hotelImgList.stream()
                    .map(HotelImg::getFileId).toList();
            ResponseResult res = fileFeignClient.addFileLink(newFileIds);
            if(res.getCode() != 200){
                throw new RuntimeException("图片绑定失败");
            }
        }
        // 插入房间
        if(hotel.getHotelRoomList() != null && !hotel.getHotelRoomList().isEmpty()){
            addHotelRoomList(hotel.getHotelRoomList(), hotelId);
        }
        return new ResponseResult<>(200, "添加成功");
    }

    @Override
    @Transactional
    public ResponseResult<Void> deleteHotel(Long id) {
        Hotel hotel = selectHotelById(id).getData();
        if(hotel == null){
            return new ResponseResult<>(400, "酒店不存在");
        }
        // 删除绑定的图片
        if(hotel.getHotelImgList() != null && !hotel.getHotelImgList().isEmpty()){
            List<Long> fileIds = hotel.getHotelImgList().stream()
                    .map(SystemFile::getId)
                    .toList();
            ResponseResult res = fileFeignClient.deleteFileLink(fileIds);
            if(res.getCode() != 200){
                throw new RuntimeException("删除图片绑定失败");
            }
            LambdaQueryWrapper<HotelImg> hotelImgQueryWrapper = new LambdaQueryWrapper<>();
            hotelImgQueryWrapper.in(HotelImg::getAsId, fileIds);
            hotelImgMapper.delete(hotelImgQueryWrapper);
        }
        // 删除所有房间
        if(hotel.getHotelRoomList() != null && !hotel.getHotelRoomList().isEmpty()){
            List<Long> roomIds = hotel.getHotelRoomList().stream()
                    .map(HotelRoom::getId)
                    .toList();
            LambdaQueryWrapper<RoomImg> roomImgQueryWrapper = new LambdaQueryWrapper<>();
            roomImgQueryWrapper.in(RoomImg::getAsId, roomIds);
            roomImgQueryWrapper.eq(RoomImg::getIsDelete, 0);
            List<RoomImg> roomImgList = roomImgMapper.selectList(roomImgQueryWrapper);
            // 集中删除所有房间绑定的图片
            if(!roomImgList.isEmpty()){
                List<Long> fileIds = roomImgList.stream()
                        .map(RoomImg::getFileId)
                        .toList();
                ResponseResult res = fileFeignClient.deleteFileLink(fileIds);
                if(res.getCode() != 200){
                    throw new RuntimeException("删除图片绑定失败");
                }
                LambdaQueryWrapper<HotelImg> hotelImgQueryWrapper = new LambdaQueryWrapper<>();
                hotelImgQueryWrapper.in(HotelImg::getFileId, fileIds);
                hotelImgMapper.delete(hotelImgQueryWrapper);
            }

            // 删除所有套餐
            LambdaUpdateWrapper<RoomTypePrice> roomTypePriceUpdateWrapper = new LambdaUpdateWrapper<>();
            roomTypePriceUpdateWrapper.in(RoomTypePrice::getAsRoom, roomIds);
            roomTypePriceUpdateWrapper.eq(RoomTypePrice::getIsDelete, 0);
            roomTypePriceUpdateWrapper.set(RoomTypePrice::getIsDelete, 1);
            roomTypePriceMapper.update(null, roomTypePriceUpdateWrapper);

            // 最后逻辑删除房间
            LambdaUpdateWrapper<HotelRoom> hotelRoomUpdateWrapper = new LambdaUpdateWrapper<>();
            hotelRoomUpdateWrapper.in(HotelRoom::getId, roomIds);
            hotelRoomUpdateWrapper.eq(HotelRoom::getIsDelete, 0);
            hotelRoomUpdateWrapper.set(HotelRoom::getIsDelete, 1);
            hotelRoomMapper.update(null, hotelRoomUpdateWrapper);
        }

        // 删除酒店
        LambdaUpdateWrapper<Hotel> hotelUpdateWrapper = new LambdaUpdateWrapper<>();
        hotelUpdateWrapper.eq(Hotel::getId, id);
        hotelUpdateWrapper.eq(Hotel::getIsDelete, 0);
        hotelUpdateWrapper.set(Hotel::getIsDelete, 1);
        hotelMapper.update(null, hotelUpdateWrapper);
        return new ResponseResult<>(200, "删除成功");
    }

    @Override
    public ResponseResult<ScrollPage> publicSearchHotels(String cursor, int limit, String sort, HotelFilter filter) {
        int pageSize = limit <= 0 ? 10 : Math.min(limit, 50);
        String sortKey = BaseTypeUtil.hasText(sort) ? sort : "recommended";

        LambdaQueryWrapper<Hotel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Hotel::getIsDelete, 0);
        if (filter != null) {
            if (BaseTypeUtil.hasText(filter.getCityCode())) {
                wrapper.eq(Hotel::getAreaCode, filter.getCityCode());
            }
            if (filter.getStarLevels() != null && !filter.getStarLevels().isEmpty()) {
                wrapper.in(Hotel::getStarLevel, filter.getStarLevels());
            }
            if (filter.getRatingMin() != null) {
                wrapper.ge(Hotel::getRating, BigDecimal.valueOf(filter.getRatingMin()));
            }
        }

        List<Hotel> hotels = hotelMapper.selectList(wrapper);
        if (hotels == null || hotels.isEmpty()) {
            return new ResponseResult<>(200, "查询成功", new ScrollPage<>(new ArrayList<>(), null));
        }

        List<Long> hotelIds = hotels.stream().map(Hotel::getId).filter(Objects::nonNull).toList();

        Map<Long, String> hotelCoverUrlMap = new HashMap<>();
        List<Long> coverFileIds = hotels.stream()
                .map(Hotel::getCoverImg)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (!coverFileIds.isEmpty()) {
            LambdaQueryWrapper<SystemFile> systemFileWrapper = new LambdaQueryWrapper<>();
            systemFileWrapper.in(SystemFile::getId, coverFileIds);
            systemFileWrapper.eq(SystemFile::getIsDelete, 0);
            List<SystemFile> coverFiles = systemFileMapper.selectList(systemFileWrapper);
            Map<Long, String> fileIdPathMap = coverFiles.stream()
                    .filter(f -> f != null && BaseTypeUtil.hasText(f.getPath()) && f.getId() != null)
                    .collect(Collectors.toMap(SystemFile::getId, SystemFile::getPath, (a, b) -> a));
            for (Hotel hotel : hotels) {
                if (hotel.getId() == null || hotel.getCoverImg() == null) {
                    continue;
                }
                String path = fileIdPathMap.get(hotel.getCoverImg());
                if (BaseTypeUtil.hasText(path)) {
                    hotelCoverUrlMap.put(hotel.getId(), ossUtil.getTmpUrl(path));
                }
            }
        }

        Map<Long, HotelRoom> roomIdMap = new HashMap<>();
        Map<Long, Long> roomHotelMap = new HashMap<>();
        List<Long> roomIds = new ArrayList<>();

        if (!hotelIds.isEmpty()) {
            LambdaQueryWrapper<HotelRoom> roomWrapper = new LambdaQueryWrapper<>();
            roomWrapper.in(HotelRoom::getAsHotel, hotelIds);
            roomWrapper.eq(HotelRoom::getIsDelete, 0);
            List<HotelRoom> rooms = hotelRoomMapper.selectList(roomWrapper);
            if (rooms != null && !rooms.isEmpty()) {
                for (HotelRoom room : rooms) {
                    if (room.getId() == null) {
                        continue;
                    }
                    roomIdMap.put(room.getId(), room);
                    roomHotelMap.put(room.getId(), room.getAsHotel());
                    roomIds.add(room.getId());
                }
            }
        }

        Map<Long, CheapestInfo> cheapestInfoMap = new HashMap<>();
        if (!roomIds.isEmpty()) {
            LambdaQueryWrapper<RoomTypePrice> priceWrapper = new LambdaQueryWrapper<>();
            priceWrapper.in(RoomTypePrice::getAsRoom, roomIds);
            priceWrapper.eq(RoomTypePrice::getIsDelete, 0);
            List<RoomTypePrice> prices = roomTypePriceMapper.selectList(priceWrapper);
            if (prices != null && !prices.isEmpty()) {
                for (RoomTypePrice price : prices) {
                    if (price == null || price.getAsRoom() == null || price.getPrice() == null) {
                        continue;
                    }
                    Long hotelId = roomHotelMap.get(price.getAsRoom());
                    if (hotelId == null) {
                        continue;
                    }
                    HotelRoom room = roomIdMap.get(price.getAsRoom());
                    if (room == null) {
                        continue;
                    }
                    CheapestInfo existing = cheapestInfoMap.get(hotelId);
                    if (existing == null
                            || price.getPrice().compareTo(existing.price) < 0
                            || (price.getPrice().compareTo(existing.price) == 0
                            && price.getAsRoom().compareTo(existing.roomId) < 0)) {
                        cheapestInfoMap.put(
                                hotelId,
                                new CheapestInfo(price.getAsRoom(), room.getName(), room.getMaxPeople(), price.getPrice())
                        );
                    }
                }
            }
        }

        List<HotelPublicItem> items = new ArrayList<>();
        for (Hotel hotel : hotels) {
            if (hotel.getId() == null) {
                continue;
            }
            CheapestInfo cheapest = cheapestInfoMap.get(hotel.getId());
            BigDecimal minPrice = cheapest == null ? null : cheapest.price;
            if (filter != null) {
                boolean hasPriceFilter = filter.getPriceMin() != null || filter.getPriceMax() != null;
                if (hasPriceFilter && minPrice == null) {
                    continue;
                }
                if (filter.getPriceMin() != null && minPrice.compareTo(BigDecimal.valueOf(filter.getPriceMin())) < 0) {
                    continue;
                }
                if (filter.getPriceMax() != null && minPrice.compareTo(BigDecimal.valueOf(filter.getPriceMax())) > 0) {
                    continue;
                }
            }
            items.add(new HotelPublicItem(
                    hotel.getId(),
                    hotel.getName(),
                    hotel.getAddress(),
                    hotel.getAreaCode(),
                    hotel.getRating(),
                    hotel.getStarLevel(),
                    hotelCoverUrlMap.getOrDefault(hotel.getId(), ""),
                    cheapest
            ));
        }

        Comparator<BigDecimal> bigDecimalDescNullLast = Comparator.nullsLast((a, b) -> b.compareTo(a));
        Comparator<BigDecimal> bigDecimalAscNullLast = Comparator.nullsLast(BigDecimal::compareTo);
        Comparator<Integer> intDescNullLast = Comparator.nullsLast(Comparator.reverseOrder());

        Comparator<HotelPublicItem> comparator;
        switch (sortKey) {
            case "price_asc":
                comparator = Comparator.comparing(HotelPublicItem::getMinPrice, bigDecimalAscNullLast)
                        .thenComparing(HotelPublicItem::getRating, bigDecimalDescNullLast)
                        .thenComparing(HotelPublicItem::getId, Comparator.reverseOrder());
                break;
            case "price_desc":
                comparator = Comparator.comparing(HotelPublicItem::getMinPrice, bigDecimalDescNullLast)
                        .thenComparing(HotelPublicItem::getRating, bigDecimalDescNullLast)
                        .thenComparing(HotelPublicItem::getId, Comparator.reverseOrder());
                break;
            case "rating_desc":
                comparator = Comparator.comparing(HotelPublicItem::getRating, bigDecimalDescNullLast)
                        .thenComparing(HotelPublicItem::getId, Comparator.reverseOrder());
                break;
            case "recommended":
            default:
                comparator = Comparator.comparing(HotelPublicItem::getRating, bigDecimalDescNullLast)
                        .thenComparing(HotelPublicItem::getStarLevel, intDescNullLast)
                        .thenComparing(HotelPublicItem::getMinPrice, bigDecimalAscNullLast)
                        .thenComparing(HotelPublicItem::getId, Comparator.reverseOrder());
                break;
        }

        items.sort(comparator);

        Long cursorId = decodeCursorId(cursor);
        int startIndex = 0;
        if (cursorId != null) {
            for (int i = 0; i < items.size(); i++) {
                if (cursorId.equals(items.get(i).id)) {
                    startIndex = i + 1;
                    break;
                }
            }
        }

        int endIndex = Math.min(startIndex + pageSize, items.size());
        List<Map<String, Object>> pageItems = new ArrayList<>();
        for (int i = startIndex; i < endIndex; i++) {
            HotelPublicItem item = items.get(i);
            Map<String, Object> m = new HashMap<>();
            m.put("id", item.id);
            m.put("name", item.name);
            m.put("address", item.address);
            m.put("cityCode", item.cityCode);
            m.put("coverImageUrl", item.coverImageUrl);
            m.put("rating", item.rating == null ? BigDecimal.ZERO : item.rating);
            m.put("reviewCount", 0);
            m.put("starLevel", item.starLevel == null ? 0 : item.starLevel);

            Map<String, Object> cheapestMap = new HashMap<>();
            CheapestInfo cheapest = item.cheapest;
            cheapestMap.put("roomName", cheapest == null ? "" : (cheapest.roomName == null ? "" : cheapest.roomName));
            cheapestMap.put("maxPeople", cheapest == null || cheapest.maxPeople == null ? 0 : cheapest.maxPeople);
            Map<String, Object> money = new HashMap<>();
            money.put("currency", "CNY");
            money.put("amount", cheapest == null || cheapest.price == null ? BigDecimal.ZERO : cheapest.price);
            cheapestMap.put("price", money);
            m.put("cheapest", cheapestMap);

            pageItems.add(m);
        }

        String nextCursor = null;
        if (endIndex < items.size() && !pageItems.isEmpty()) {
            HotelPublicItem last = items.get(endIndex - 1);
            nextCursor = encodeCursorId(last.id);
        }

        ScrollPage<Map<String, Object>> page = new ScrollPage<>(pageItems, nextCursor);
        return new ResponseResult<>(200, "查询成功", page);
    }

    private static String encodeCursorId(Long id) {
        if (id == null) {
            return null;
        }
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(String.valueOf(id).getBytes(StandardCharsets.UTF_8));
    }

    private static Long decodeCursorId(String cursor) {
        if (!BaseTypeUtil.hasText(cursor)) {
            return null;
        }
        try {
            byte[] decoded = Base64.getUrlDecoder().decode(cursor);
            String s = new String(decoded, StandardCharsets.UTF_8);
            if (!BaseTypeUtil.hasText(s)) {
                return null;
            }
            return Long.valueOf(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private static class CheapestInfo {
        private final Long roomId;
        private final String roomName;
        private final Integer maxPeople;
        private final BigDecimal price;

        private CheapestInfo(Long roomId, String roomName, Integer maxPeople, BigDecimal price) {
            this.roomId = roomId;
            this.roomName = roomName;
            this.maxPeople = maxPeople;
            this.price = price;
        }
    }

    private static class HotelPublicItem {
        private final Long id;
        private final String name;
        private final String address;
        private final String cityCode;
        private final BigDecimal rating;
        private final Integer starLevel;
        private final String coverImageUrl;
        private final CheapestInfo cheapest;

        private HotelPublicItem(Long id,
                               String name,
                               String address,
                               String cityCode,
                               BigDecimal rating,
                               Integer starLevel,
                               String coverImageUrl,
                               CheapestInfo cheapest) {
            this.id = id;
            this.name = name;
            this.address = address;
            this.cityCode = cityCode;
            this.rating = rating;
            this.starLevel = starLevel;
            this.coverImageUrl = coverImageUrl;
            this.cheapest = cheapest;
        }

        private Long getId() {
            return id;
        }

        private BigDecimal getRating() {
            return rating;
        }

        private Integer getStarLevel() {
            return starLevel;
        }

        private BigDecimal getMinPrice() {
            return cheapest == null ? null : cheapest.price;
        }
    }

    private void addHotelRoomList(List<HotelRoom> hotelRoomList, Long hotelId){
        if(hotelRoomList == null || hotelRoomList.isEmpty()){
            return;
        }
        // 新增房间信息
        hotelRoomList.forEach(room -> {
            room.setAsHotel(hotelId);
            room.setUpdateBy(SystemCommonUtil.getNowUserId());
            room.setIsDelete(0);
        });
        hotelRoomMapper.insertBatchSomeColumn(hotelRoomList);

        // 新增房间图片信息
        List<RoomImg> roomImgList = new ArrayList<>();
        for(HotelRoom room : hotelRoomList){
            List<SystemFile> imgList = room.getRoomImgList();
            if(imgList == null) {
                continue;
            }
            List<RoomImg> tmp = imgList.stream()
                    .map(img -> {
                        RoomImg roomImg = new RoomImg();
                        roomImg.setAsId(room.getId());
                        roomImg.setFileId(img.getId());
                        roomImg.setUpdateBy(SystemCommonUtil.getNowUserId());
                        roomImg.setIsDelete(0);
                        return roomImg;
                    })
                    .toList();
            roomImgList.addAll(tmp);
        }
        if(!roomImgList.isEmpty()){
            roomImgMapper.insertBatchSomeColumn(roomImgList);

            // 更新link
            List<Long> imgIds = roomImgList.stream()
                    .map(RoomImg::getFileId)
                    .toList();
            ResponseResult res = fileFeignClient.addFileLink(imgIds);
            if (!res.getCode().equals(200)) {
                throw new BusinessException(400, "添加图片绑定失败");
            }
        }

        // 新增房间套餐
        List<RoomTypePrice> roomTypePriceList = new ArrayList<>();
        for(HotelRoom room : hotelRoomList){
            List<RoomTypePrice> tmp = room.getRoomTypePriceList();
            if(tmp == null || tmp.isEmpty()) {
                continue;
            }
            tmp.forEach(price -> {
                price.setAsRoom(room.getId());
                price.setUpdateBy(SystemCommonUtil.getNowUserId());
                price.setIsDelete(0);
            });
            roomTypePriceList.addAll(tmp);
        }
        roomTypePriceMapper.insertBatchSomeColumn(roomTypePriceList);
    }

    private void deleteHotelRoomByIds(List<Long> deleteRoomIds){
        // 查找需要删除的房间所绑定的图片id
        LambdaQueryWrapper<RoomImg> roomImgQueryWrapper = new LambdaQueryWrapper<>();
        roomImgQueryWrapper.in(RoomImg::getAsId, deleteRoomIds);
        roomImgQueryWrapper.eq(RoomImg::getIsDelete, 0);
        List<Long> deleteRoomImgIds = roomImgMapper.selectList(roomImgQueryWrapper).stream()
                .map(RoomImg::getFileId)
                .toList();
        // 删除图片link
        ResponseResult res = fileFeignClient.deleteFileLink(deleteRoomImgIds);
        if (!res.getCode().equals(200)) {
            throw new BusinessException(400, "删除图片绑定失败");
        }
        // 删除图片绑定关系
        roomImgMapper.delete(roomImgQueryWrapper);
        // 删除套餐
        LambdaUpdateWrapper<RoomTypePrice> roomTypePriceUpdateWrapper = new LambdaUpdateWrapper<>();
        roomTypePriceUpdateWrapper.in(RoomTypePrice::getAsRoom, deleteRoomIds);
        roomTypePriceUpdateWrapper.eq(RoomTypePrice::getIsDelete, 0);
        roomTypePriceUpdateWrapper.set(RoomTypePrice::getIsDelete, 1);
        roomTypePriceMapper.update(null, roomTypePriceUpdateWrapper);
        // 删除所有房间(逻辑删除)
        LambdaUpdateWrapper<HotelRoom> roomUpdateWrapper = new LambdaUpdateWrapper<>();
        roomUpdateWrapper.in(HotelRoom::getId, deleteRoomIds);
        roomUpdateWrapper.eq(HotelRoom::getIsDelete, 0);
        roomUpdateWrapper.set(HotelRoom::getIsDelete, 1);
        hotelRoomMapper.update(null, roomUpdateWrapper);
    }

    private void updateHotelRoomList(List<HotelRoom> hotelRoomList){
        long nowUserId = SystemCommonUtil.getNowUserId();

        // 更新基础信息
        for(HotelRoom hotelRoom : hotelRoomList){
            hotelRoom.setUpdateBy(nowUserId);
            LambdaUpdateWrapper<HotelRoom> hotelRoomWrapper = new LambdaUpdateWrapper<>();
            hotelRoomWrapper.eq(HotelRoom::getId, hotelRoom.getId());
            hotelRoomWrapper.eq(HotelRoom::getIsDelete, 0);
            int x = hotelRoomMapper.update(hotelRoom, hotelRoomWrapper);
        }

        // 构建图片更新map并更新
        Map<Long, List<SystemFile>> newSystemFileMap = hotelRoomList.stream()
                .filter(room -> room.getRoomImgList() != null)
                .collect(Collectors.toMap(HotelRoom::getId, HotelRoom::getRoomImgList));

        wrapperUtil.updateImageBind(newSystemFileMap, roomImgMapper, RoomImg::new);

        // 逻辑删除所有套餐
        List<Long> updateRoomIds = hotelRoomList.stream()
                .map(HotelRoom::getId)
                .toList();
        LambdaUpdateWrapper<RoomTypePrice> roomTypePriceUpdateWrapper = new LambdaUpdateWrapper<>();
        roomTypePriceUpdateWrapper.in(RoomTypePrice::getAsRoom, updateRoomIds);
        roomTypePriceUpdateWrapper.eq(RoomTypePrice::getIsDelete, 0);
        roomTypePriceUpdateWrapper.set(RoomTypePrice::getIsDelete, 1);
        roomTypePriceMapper.update(null, roomTypePriceUpdateWrapper);

        // 添加新的套餐
        List<RoomTypePrice> roomTypePriceList = new ArrayList<>();
        for(HotelRoom hotelRoom : hotelRoomList){
            List<RoomTypePrice> tmp = hotelRoom.getRoomTypePriceList();
            if(tmp != null){
                tmp.forEach(roomTypePrice -> {
                    roomTypePrice.setAsRoom(hotelRoom.getId());
                    roomTypePrice.setUpdateBy(nowUserId);
                    roomTypePrice.setIsDelete(0);
                });
                roomTypePriceList.addAll(tmp);
            }
        }
        if(!roomTypePriceList.isEmpty()){
            roomTypePriceMapper.insertBatchSomeColumn(roomTypePriceList);
        }

    }

}
