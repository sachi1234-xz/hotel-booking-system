package com.hotel.hotelservice.exception;

public class HotelNotFoundException extends RuntimeException {

    public HotelNotFoundException(Long id) {
        super("Hotel with id " + id + " not found");
    }
}
