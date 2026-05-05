package org.example.service;

import org.example.domain.encapsulate.ResponseResult;
import org.example.domain.encapsulate.ScrollPage;
import org.example.domain.encapsulate.TableData;
import org.example.domain.hotel.Hotel;
import org.example.domain.hotel.HotelFilter;
import org.example.domain.hotel.HotelSelectDTO;

public interface HotelService {
    ResponseResult<TableData<Hotel>> selectHotelList(HotelSelectDTO hotelSelectDTO);

    ResponseResult<Hotel> selectHotelById(Long id);

    ResponseResult<Void> updateHotel(Hotel hotel);

    ResponseResult<Void> insertHotel(Hotel hotel);

    ResponseResult<Void> deleteHotel(Long id);

    ResponseResult<ScrollPage> publicSearchHotels(String cursor, int limit, String sort, HotelFilter filter);
}
