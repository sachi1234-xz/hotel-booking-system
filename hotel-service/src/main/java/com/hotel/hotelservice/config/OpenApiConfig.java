package com.hotel.hotelservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "X-API-KEY";

    @Bean
    public OpenAPI hotelServiceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Hotel Service API")
                        .version("v1.0.0")
                        .description("REST API for managing hotels and rooms in the Hotel Booking System. "
                                + "All endpoints except Swagger UI and API docs require the X-API-KEY header."))
                .components(new Components().addSecuritySchemes(SECURITY_SCHEME_NAME,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("X-API-KEY")
                                .description("API key used to authenticate requests to the Hotel Service")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));
    }
}
