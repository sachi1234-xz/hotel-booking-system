package com.hotel.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomAvailabilityResponse {

    private Long id;
    private String roomNumber;
    private String type;
    private BigDecimal pricePerNight;
    private boolean available;
    private Long hotelId;
}
