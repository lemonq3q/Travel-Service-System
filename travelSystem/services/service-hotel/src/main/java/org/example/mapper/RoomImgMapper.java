package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.config.mybatisplus.BatchBaseMapper;
import org.example.domain.hotel.RoomImg;

@Mapper
public interface RoomImgMapper extends BatchBaseMapper<RoomImg> {
}
