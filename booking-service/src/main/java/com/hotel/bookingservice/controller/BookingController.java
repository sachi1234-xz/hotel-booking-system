package com.hotel.bookingservice.controller;

import com.hotel.bookingservice.dto.BookingRequest;
import com.hotel.bookingservice.model.Booking;
import com.hotel.bookingservice.model.BookingStatus;
import com.hotel.bookingservice.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    public List<Booking> getAllBookings(
            @RequestParam(required = false) String roomNumber,
            @RequestParam(required = false) String guestName) {
        if (roomNumber != null) {
            return bookingService.getBookingsByRoom(roomNumber);
        }
        if (guestName != null) {
            return bookingService.searchByGuestName(guestName);
        }
        return bookingService.getAllBookings();
    }

    @GetMapping("/{id}")
    public Booking getBooking(@PathVariable String id) {
        return bookingService.getBookingById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Booking createBooking(@Valid @RequestBody BookingRequest request) {
        return bookingService.createBooking(request);
    }

    @PutMapping("/{id}")
    public Booking updateBooking(@PathVariable String id, @Valid @RequestBody BookingRequest request) {
        return bookingService.updateBooking(id, request);
    }

    @PatchMapping("/{id}/status")
    public Booking updateStatus(@PathVariable String id, @RequestParam BookingStatus status) {
        return bookingService.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable String id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }
}
