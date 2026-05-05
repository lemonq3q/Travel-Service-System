package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.config.mybatisplus.BatchBaseMapper;
import org.example.domain.hotel.RoomStock;

@Mapper
public interface RoomStockMapper extends BatchBaseMapper<RoomStock> {
}
