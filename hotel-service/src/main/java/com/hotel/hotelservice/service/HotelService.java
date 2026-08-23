package com.hotel.hotelservice.service;

import com.hotel.hotelservice.entity.Hotel;

import java.util.List;

public interface HotelService {

    Hotel createHotel(Hotel hotel);

    List<Hotel> getAllHotels();

    Hotel getHotelById(Long id);

    Hotel updateHotel(Long id, Hotel hotelDetails);

    void deleteHotel(Long id);
}
