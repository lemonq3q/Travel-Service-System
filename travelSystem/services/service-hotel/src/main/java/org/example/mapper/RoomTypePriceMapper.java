package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.config.mybatisplus.BatchBaseMapper;
import org.example.domain.hotel.RoomTypePrice;

@Mapper
public interface RoomTypePriceMapper extends BatchBaseMapper<RoomTypePrice> {
}
