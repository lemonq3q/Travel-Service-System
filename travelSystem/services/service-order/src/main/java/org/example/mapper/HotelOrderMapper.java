package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.config.mybatisplus.BatchBaseMapper;
import org.example.domain.order.HotelOrder;

@Mapper
public interface HotelOrderMapper extends BatchBaseMapper<HotelOrder> {
}
