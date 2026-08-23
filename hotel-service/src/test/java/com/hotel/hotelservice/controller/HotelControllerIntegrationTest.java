package com.hotel.hotelservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class HotelControllerIntegrationTest {

    private static final String VALID_API_KEY = "HOTEL_SECRET_67890";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void requestWithoutApiKeyIsRejected() throws Exception {
        mockMvc.perform(get("/api/hotels"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", is("Unauthorized")));
    }

    @Test
    void requestWithWrongApiKeyIsRejected() throws Exception {
        mockMvc.perform(get("/api/hotels").header("X-API-KEY", "WRONG_KEY"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getHotelsWithValidApiKeyReturnsSeededHotels() throws Exception {
        mockMvc.perform(get("/api/hotels").header("X-API-KEY", VALID_API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$[*].name", org.hamcrest.Matchers.hasItem("Grand Plaza Hotel")));
    }

    @Test
    void getHotelByIdReturnsRooms() throws Exception {
        mockMvc.perform(get("/api/hotels/1").header("X-API-KEY", VALID_API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.rooms.length()", greaterThanOrEqualTo(1)));
    }

    @Test
    void getUnknownHotelReturns404() throws Exception {
        mockMvc.perform(get("/api/hotels/9999").header("X-API-KEY", VALID_API_KEY))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", org.hamcrest.Matchers.containsString("not found")));
    }

    @Test
    void createHotelReturns201() throws Exception {
        String body = """
                {"name": "Integration Hotel", "location": "Paris", "description": "Test hotel"}
                """;
        mockMvc.perform(post("/api/hotels")
                        .header("X-API-KEY", VALID_API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Integration Hotel")))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void createHotelWithBlankNameReturns400() throws Exception {
        String body = """
                {"name": "", "location": "Paris"}
                """;
        mockMvc.perform(post("/api/hotels")
                        .header("X-API-KEY", VALID_API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.name").exists());
    }

    @Test
    void updateHotelReturnsUpdatedHotel() throws Exception {
        String body = """
                {"name": "Grand Plaza Renamed", "location": "New York", "description": "Updated"}
                """;
        mockMvc.perform(put("/api/hotels/1")
                        .header("X-API-KEY", VALID_API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Grand Plaza Renamed")));
    }

    @Test
    void deleteHotelReturns204() throws Exception {
        mockMvc.perform(delete("/api/hotels/999999").header("X-API-KEY", VALID_API_KEY))
                .andExpect(status().isNotFound());

        String body = """
                {"name": "Temp Delete Hotel", "location": "Berlin"}
                """;
        String location = mockMvc.perform(post("/api/hotels")
                        .header("X-API-KEY", VALID_API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        long createdId = objectMapper.readTree(location).path("id").asLong();

        mockMvc.perform(delete("/api/hotels/" + createdId).header("X-API-KEY", VALID_API_KEY))
                .andExpect(status().isNoContent());
    }

    @Test
    void addRoomToHotelReturns201() throws Exception {
        String body = """
                {"roomNumber": "501", "type": "SUITE", "pricePerNight": 550.00, "isAvailable": true}
                """;
        mockMvc.perform(post("/api/hotels/1/rooms")
                        .header("X-API-KEY", VALID_API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roomNumber", is("501")))
                .andExpect(jsonPath("$.id").exists());

        mockMvc.perform(get("/api/hotels/1/rooms").header("X-API-KEY", VALID_API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].roomNumber", org.hamcrest.Matchers.hasItem("501")));
    }

    @Test
    void getRoomsOfUnknownHotelReturns404() throws Exception {
        mockMvc.perform(get("/api/hotels/9999/rooms").header("X-API-KEY", VALID_API_KEY))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAvailableRoomsReturnsOnlyAvailableRooms() throws Exception {
        mockMvc.perform(get("/api/rooms/available").header("X-API-KEY", VALID_API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[*].isAvailable", everyItem(is(true))));
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
    void updateRoomReturnsUpdatedRoom() throws Exception {
        String body = """
                {"roomNumber": "101", "type": "SUITE", "pricePerNight": 299.99, "available": false}
                """;
        mockMvc.perform(put("/api/rooms/1")
                        .header("X-API-KEY", VALID_API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type", is("SUITE")))
                .andExpect(jsonPath("$.pricePerNight", is(299.99)))
                .andExpect(jsonPath("$.available", is(false)));
    }

    @Test
    void updateUnknownRoomReturns404() throws Exception {
        String body = """
                {"roomNumber": "999", "type": "SINGLE", "pricePerNight": 100.00, "isAvailable": true}
                """;
        mockMvc.perform(put("/api/rooms/9999")
                        .header("X-API-KEY", VALID_API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", org.hamcrest.Matchers.containsString("not found")));
    }

    @Test
    void updateRoomWithBlankRoomNumberReturns400() throws Exception {
        String body = """
                {"roomNumber": "", "type": "SINGLE", "pricePerNight": 100.00, "isAvailable": true}
                """;
        mockMvc.perform(put("/api/rooms/1")
                        .header("X-API-KEY", VALID_API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.roomNumber").exists());
    }

    @Test
    void deleteRoomReturns204() throws Exception {
        mockMvc.perform(delete("/api/rooms/999999").header("X-API-KEY", VALID_API_KEY))
                .andExpect(status().isNotFound());

        String body = """
                {"roomNumber": "T1", "type": "SINGLE", "pricePerNight": 75.00, "isAvailable": true}
                """;
        String location = mockMvc.perform(post("/api/hotels/1/rooms")
                        .header("X-API-KEY", VALID_API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        long createdId = objectMapper.readTree(location).path("id").asLong();

        mockMvc.perform(delete("/api/rooms/" + createdId).header("X-API-KEY", VALID_API_KEY))
                .andExpect(status().isNoContent());
    }
}
