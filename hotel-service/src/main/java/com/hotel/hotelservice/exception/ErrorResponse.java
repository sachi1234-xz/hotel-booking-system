package com.hotel.hotelservice.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "Standard error response returned by the API")
public record ErrorResponse(
        @Schema(description = "HTTP status code", example = "404") int status,
        @Schema(description = "Short error message", example = "Not Found") String error,
        @Schema(description = "Human readable error detail", example = "Hotel with id 1 not found") String message,
        @Schema(description = "Timestamp when the error occurred") LocalDateTime timestamp,
        @Schema(description = "Field validation errors (only for 400 responses)") Map<String, String> fieldErrors) {

    public static ErrorResponse of(int status, String error, String message) {
        return new ErrorResponse(status, error, message, LocalDateTime.now(), null);
    }

    public static ErrorResponse of(int status, String error, String message, Map<String, String> fieldErrors) {
        return new ErrorResponse(status, error, message, LocalDateTime.now(), fieldErrors);
    }
}
