package com.hotel.booking.service.impl;

import com.hotel.booking.dto.BookingRequest;
import com.hotel.booking.dto.BookingResponse;
import com.hotel.booking.dto.RoomAvailabilityResponse;
import com.hotel.booking.entity.Booking;
import com.hotel.booking.entity.BookingStatus;
import com.hotel.booking.exception.BookingNotFoundException;
import com.hotel.booking.exception.HotelServiceException;
import com.hotel.booking.exception.RoomNotAvailableException;
import com.hotel.booking.repository.BookingRepository;
import com.hotel.booking.service.BookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingServiceImpl.class);

    private final BookingRepository bookingRepository;
    private final WebClient webClient;

    public BookingServiceImpl(BookingRepository bookingRepository, WebClient webClient) {
        this.bookingRepository = bookingRepository;
        this.webClient = webClient;
    }

    @Override
    @Transactional
    public BookingResponse createBooking(BookingRequest request, Long userId) {
        RoomAvailabilityResponse room = checkRoomAvailability(request.getRoomId());

        if (!room.isAvailable()) {
            throw new RoomNotAvailableException(request.getRoomId());
        }

        long nights = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        if (nights <= 0) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }

        Booking booking = Booking.builder()
                .userId(userId)
                .hotelId(request.getHotelId())
                .roomId(request.getRoomId())
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .numberOfGuests(request.getNumberOfGuests())
                .totalPrice(room.getPricePerNight().multiply(java.math.BigDecimal.valueOf(nights)))
                .status(BookingStatus.CONFIRMED)
                .build();

        Booking saved = bookingRepository.save(booking);
        log.info("Created booking {} for user {} in room {}", saved.getId(), userId, request.getRoomId());

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));
        return toResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingResponse cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking is already cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        Booking saved = bookingRepository.save(booking);
        log.info("Cancelled booking {}", id);

        return toResponse(saved);
    }

    private RoomAvailabilityResponse checkRoomAvailability(Long roomId) {
        try {
            return webClient.get()
                    .uri("/api/rooms/{id}", roomId)
                    .retrieve()
                    .bodyToMono(RoomAvailabilityResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().is4xxClientError()) {
                throw new HotelServiceException("Room with id " + roomId + " not found in Hotel Service");
            }
            throw new HotelServiceException("Hotel Service unavailable: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new HotelServiceException("Failed to connect to Hotel Service: " + e.getMessage(), e);
        }
    }

    private BookingResponse toResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .userId(booking.getUserId())
                .hotelId(booking.getHotelId())
                .roomId(booking.getRoomId())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .numberOfGuests(booking.getNumberOfGuests())
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus().name())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}
