package com.hotel.bookingservice.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingRequest {

    @NotBlank(message = "guestName is required")
    private String guestName;

    @NotBlank(message = "roomNumber is required")
    private String roomNumber;

    @NotNull(message = "checkInDate is required")
    private LocalDate checkInDate;

    @NotNull(message = "checkOutDate is required")
    @Future(message = "checkOutDate must be in the future")
    private LocalDate checkOutDate;
}
