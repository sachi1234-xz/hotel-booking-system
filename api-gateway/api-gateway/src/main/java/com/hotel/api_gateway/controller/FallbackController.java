package com.hotel.api_gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/auth")
    public Mono<Map<String, Object>> authFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "ERROR");
        response.put("message", "Auth Service is currently unavailable. Please try again later.");
        response.put("timestamp", System.currentTimeMillis());
        return Mono.just(response);
    }

    @GetMapping("/hotel")
    public Mono<Map<String, Object>> hotelFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "ERROR");
        response.put("message", "Hotel Service is currently unavailable. Please try again later.");
        response.put("timestamp", System.currentTimeMillis());
        return Mono.just(response);
    }

    @GetMapping("/booking")
    public Mono<Map<String, Object>> bookingFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "ERROR");
        response.put("message", "Booking Service is currently unavailable. Please try again later.");
        response.put("timestamp", System.currentTimeMillis());
        return Mono.just(response);
    }

    @GetMapping("/payment")
    public Mono<Map<String, Object>> paymentFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "ERROR");
        response.put("message", "Payment Service is currently unavailable. Please try again later.");
        response.put("timestamp", System.currentTimeMillis());
        return Mono.just(response);
    }
}
