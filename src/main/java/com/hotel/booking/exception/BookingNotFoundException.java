package com.hotel.booking.exception;

import org.springframework.http.HttpStatus;

public class BookingNotFoundException extends RuntimeException {

    public BookingNotFoundException(Long id) {
        super("Booking with id " + id + " not found");
    }
}
