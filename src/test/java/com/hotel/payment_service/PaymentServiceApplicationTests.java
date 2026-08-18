package com.hotel.payment_service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
class PaymentServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    private static final String API_KEY = "PAYMENT_SECRET_54321";
    private static final String PAYMENTS_URL = "/api/payments";

    @Test
    void contextLoads() {
    }

    @Test
    void shouldReturn401WithoutApiKey() throws Exception {
        mockMvc.perform(post(PAYMENTS_URL + "/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookingId\":1,\"userId\":1,\"amount\":150.00,\"currency\":\"USD\",\"paymentMethod\":\"CARD\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn401WithInvalidApiKey() throws Exception {
        mockMvc.perform(post(PAYMENTS_URL + "/process")
                        .header("X-API-KEY", "WRONG_KEY")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookingId\":1,\"userId\":1,\"amount\":150.00,\"currency\":\"USD\",\"paymentMethod\":\"CARD\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldProcessPaymentSuccessfully() throws Exception {
        mockMvc.perform(post(PAYMENTS_URL + "/process")
                        .header("X-API-KEY", API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookingId\":1,\"userId\":1,\"amount\":150.00,\"currency\":\"USD\",\"paymentMethod\":\"CARD\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.bookingId").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.amount").value(150.00))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.paymentMethod").value("CARD"))
                .andExpect(jsonPath("$.transactionRef").exists());
    }

    @Test
    void shouldGetPaymentHistoryForUser() throws Exception {
        mockMvc.perform(post(PAYMENTS_URL + "/process")
                        .header("X-API-KEY", API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookingId\":1,\"userId\":99,\"amount\":200.00,\"currency\":\"USD\",\"paymentMethod\":\"PAYPAL\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(get(PAYMENTS_URL + "/history")
                        .header("X-API-KEY", API_KEY)
                        .param("userId", "99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].userId").value(99));
    }

    @Test
    void shouldReturn404ForNonExistentPayment() throws Exception {
        mockMvc.perform(get(PAYMENTS_URL + "/99999")
                        .header("X-API-KEY", API_KEY))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnInvoiceForPayment() throws Exception {
        String response = mockMvc.perform(post(PAYMENTS_URL + "/process")
                        .header("X-API-KEY", API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookingId\":2,\"userId\":2,\"amount\":300.00,\"currency\":\"USD\",\"paymentMethod\":\"BANK_TRANSFER\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String paymentId = response.replaceAll(".*\"id\":(\\d+).*", "$1");

        mockMvc.perform(get(PAYMENTS_URL + "/" + paymentId + "/invoice")
                        .header("X-API-KEY", API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.invoiceNumber").exists())
                .andExpect(jsonPath("$.amount").value(300.00))
                .andExpect(jsonPath("$.paymentId").value(Long.parseLong(paymentId)));
    }

    @Test
    void shouldReturn400ForInvalidRequestBody() throws Exception {
        mockMvc.perform(post(PAYMENTS_URL + "/process")
                        .header("X-API-KEY", API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookingId\":1,\"userId\":1,\"amount\":-50.00,\"currency\":\"USD\",\"paymentMethod\":\"CARD\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400ForMissingFields() throws Exception {
        mockMvc.perform(post(PAYMENTS_URL + "/process")
                        .header("X-API-KEY", API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
