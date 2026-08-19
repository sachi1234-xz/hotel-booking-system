package com.hotel.hotelservice.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Type of room offered by a hotel", enumAsRef = true)
public enum RoomType {
    SINGLE,
    DOUBLE,
    SUITE
}
