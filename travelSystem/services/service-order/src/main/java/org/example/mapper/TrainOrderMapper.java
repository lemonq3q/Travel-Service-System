package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.config.mybatisplus.BatchBaseMapper;
import org.example.domain.order.TrainOrder;

@Mapper
public interface TrainOrderMapper extends BatchBaseMapper<TrainOrder> {
}
