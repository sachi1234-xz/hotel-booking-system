package com.hotel.api_gateway.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class ApiKeyInjectFilter implements GatewayFilter {

    @Value("${app.api-keys.auth}")
    private String authApiKey;

    @Value("${app.api-keys.hotel}")
    private String hotelApiKey;

    @Value("${app.api-keys.booking}")
    private String bookingApiKey;

    @Value("${app.api-keys.payment}")
    private String paymentApiKey;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        String apiKey = null;

        if (path.contains("/auth")) {
            apiKey = authApiKey;
        } else if (path.contains("/hotels") || path.contains("/rooms")) {
            apiKey = hotelApiKey;
        } else if (path.contains("/bookings")) {
            apiKey = bookingApiKey;
        } else if (path.contains("/payments")) {
            apiKey = paymentApiKey;
        }

        if (apiKey != null) {
            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(exchange.getRequest().mutate()
                            .header("X-API-KEY", apiKey)
                            .build())
                    .build();
            return chain.filter(mutatedExchange);
        }

        return chain.filter(exchange);
    }
}