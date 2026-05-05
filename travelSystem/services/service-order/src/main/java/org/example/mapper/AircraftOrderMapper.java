package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.config.mybatisplus.BatchBaseMapper;
import org.example.domain.order.AircraftOrder;

@Mapper
public interface AircraftOrderMapper extends BatchBaseMapper<AircraftOrder> {
}
