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
                                .stripPrefix(1)
                                .filter(jwtAuthenticationFilter)
                                .filter(apiKeyInjectFilter))
                        .uri("http://auth-service:8081"))
                .route("hotel-service", r -> r
                        .path("/api/hotels/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .filter(apiKeyInjectFilter))
                        .uri("http://hotel-service:8082"))
                .route("room-service", r -> r
                        .path("/api/rooms/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .filter(apiKeyInjectFilter))
                        .uri("http://hotel-service:8082"))
                .route("booking-service", r -> r
                        .path("/api/bookings/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .filter(jwtAuthenticationFilter)
                                .filter(apiKeyInjectFilter))
                        .uri("http://booking-service:8083"))
                .route("payment-service", r -> r
                        .path("/api/payments/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .filter(apiKeyInjectFilter))
                        .uri("http://payment-service:8084"))
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
