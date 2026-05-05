package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.config.mybatisplus.BatchBaseMapper;
import org.example.domain.order.ScenicOrder;

@Mapper
public interface ScenicOrderMapper extends BatchBaseMapper<ScenicOrder> {
}
