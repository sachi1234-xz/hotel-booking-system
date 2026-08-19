package com.hotel.booking.service;

import com.hotel.booking.dto.BookingRequest;
import com.hotel.booking.dto.BookingResponse;

import java.util.List;

public interface BookingService {

    BookingResponse createBooking(BookingRequest request, Long userId);

    BookingResponse getBookingById(Long id);

    List<BookingResponse> getBookingsByUserId(Long userId);

    BookingResponse cancelBooking(Long id);
}
