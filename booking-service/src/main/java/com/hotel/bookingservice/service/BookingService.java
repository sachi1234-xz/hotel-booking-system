package com.hotel.bookingservice.service;

import com.hotel.bookingservice.dto.BookingRequest;
import com.hotel.bookingservice.exception.BookingNotFoundException;
import com.hotel.bookingservice.model.Booking;
import com.hotel.bookingservice.model.BookingStatus;
import com.hotel.bookingservice.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Booking getBookingById(String id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));
    }

    public List<Booking> getBookingsByRoom(String roomNumber) {
        return bookingRepository.findByRoomNumber(roomNumber);
    }

    public List<Booking> searchByGuestName(String guestName) {
        return bookingRepository.findByGuestNameContainingIgnoreCase(guestName);
    }

    public Booking createBooking(BookingRequest request) {
        Booking booking = Booking.builder()
                .guestName(request.getGuestName())
                .roomNumber(request.getRoomNumber())
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .status(BookingStatus.CONFIRMED)
                .build();
        return bookingRepository.save(booking);
    }

    public Booking updateBooking(String id, BookingRequest request) {
        Booking existing = getBookingById(id);
        existing.setGuestName(request.getGuestName());
        existing.setRoomNumber(request.getRoomNumber());
        existing.setCheckInDate(request.getCheckInDate());
        existing.setCheckOutDate(request.getCheckOutDate());
        return bookingRepository.save(existing);
    }

    public Booking updateStatus(String id, BookingStatus status) {
        Booking existing = getBookingById(id);
        existing.setStatus(status);
        return bookingRepository.save(existing);
    }

    public void deleteBooking(String id) {
        Booking existing = getBookingById(id);
        bookingRepository.delete(existing);
    }
}
