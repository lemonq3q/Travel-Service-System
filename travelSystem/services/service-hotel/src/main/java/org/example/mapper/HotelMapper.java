package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.config.mybatisplus.BatchBaseMapper;
import org.example.domain.hotel.Hotel;

@Mapper
public interface HotelMapper extends BatchBaseMapper<Hotel> {

    Hotel selectHotelById(Long id);

}
