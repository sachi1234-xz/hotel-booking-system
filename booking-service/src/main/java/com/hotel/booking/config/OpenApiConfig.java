package com.hotel.booking.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String API_KEY_SCHEME = "X-API-KEY";

    @Bean
    public OpenAPI bookingServiceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Booking Service API")
                        .version("v1.0.0")
                        .description("REST API for managing hotel bookings in the Hotel Booking System. "
                                + "All endpoints require the X-API-KEY header and a valid JWT in the Authorization header."))
                .components(new Components().addSecuritySchemes(API_KEY_SCHEME,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("X-API-KEY")
                                .description("API key for inter-service authentication")))
                .addSecurityItem(new SecurityRequirement().addList(API_KEY_SCHEME));
    }
}
