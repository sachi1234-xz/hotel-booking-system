package com.hotel.api_gateway.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import com.hotel.api_gateway.filter.ApiKeyInjectFilter;
import com.hotel.api_gateway.filter.JwtAuthenticationFilter;

@Configuration
public class GatewayConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ApiKeyInjectFilter apiKeyInjectFilter;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r
                        .path("/api/auth/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .filter(apiKeyInjectFilter)
                                .circuitBreaker(config -> config
                                        .setName("authServiceCB")
                                        .setFallbackUri("forward:/fallback/auth")))
                        .uri("http://localhost:8081"))
                .route("hotel-service", r -> r
                        .path("/api/hotels/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .filter(apiKeyInjectFilter)
                                .circuitBreaker(config -> config
                                        .setName("hotelServiceCB")
                                        .setFallbackUri("forward:/fallback/hotel")))
                        .uri("http://localhost:8082"))
                .route("booking-service", r -> r
                        .path("/api/bookings/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .filter(apiKeyInjectFilter)
                                .circuitBreaker(config -> config
                                        .setName("bookingServiceCB")
                                        .setFallbackUri("forward:/fallback/booking")))
                        .uri("http://localhost:8083"))
                .route("payment-service", r -> r
                        .path("/api/payments/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .filter(apiKeyInjectFilter)
                                .circuitBreaker(config -> config
                                        .setName("paymentServiceCB")
                                        .setFallbackUri("forward:/fallback/payment")))
                        .uri("http://localhost:8084"))
                .build();
    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",
            "http://localhost:4200",
            "http://127.0.0.1:5500",      // ✅ Live Server URL 1
            "http://localhost:5500"       // ✅ Live Server URL 2
        ));
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        corsConfig.setAllowedHeaders(Arrays.asList("*"));
        corsConfig.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}
