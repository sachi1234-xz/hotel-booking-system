package com.hotel.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
@Transactional
class BookingControllerIntegrationTest {

    private static final String VALID_API_KEY = "BOOKING_SECRET_11111";
    private static final String BOOKINGS_URL = "/bookings";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void requestWithoutApiKeyIsRejected() throws Exception {
        mockMvc.perform(get(BOOKINGS_URL))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", org.hamcrest.Matchers.is("Unauthorized")));
    }

    @Test
    void requestWithWrongApiKeyIsRejected() throws Exception {
        mockMvc.perform(get(BOOKINGS_URL).header("X-API-KEY", "WRONG_KEY"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void swaggerUiIsAccessibleWithoutApiKey() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void apiDocsAreAccessibleWithoutApiKey() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }

    @Test
    void getBookingsReturnsEmptyList() throws Exception {
        mockMvc.perform(get(BOOKINGS_URL).header("X-API-KEY", VALID_API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()", org.hamcrest.Matchers.is(0)));
    }

    @Test
    void createBookingWithMissingFieldsReturns400() throws Exception {
        String body = "{}";
        mockMvc.perform(post(BOOKINGS_URL)
                        .header("X-API-KEY", VALID_API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors").exists());
    }

    @Test
    void getBookingByNonExistentIdReturns404() throws Exception {
        mockMvc.perform(get(BOOKINGS_URL + "/9999").header("X-API-KEY", VALID_API_KEY))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", org.hamcrest.Matchers.containsString("not found")));
    }
}
