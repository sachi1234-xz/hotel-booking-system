package com.hotel.booking.exception;

public class RoomNotAvailableException extends RuntimeException {

    public RoomNotAvailableException(Long roomId) {
        super("Room with id " + roomId + " is not available for the selected dates");
    }
}
