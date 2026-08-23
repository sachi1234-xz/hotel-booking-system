package com.hotel.payment_service.controller;

import com.hotel.payment_service.dto.InvoiceResponse;
import com.hotel.payment_service.dto.PaymentRequest;
import com.hotel.payment_service.dto.PaymentResponse;
import com.hotel.payment_service.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payment Service", description = "Payment processing and invoice management")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/process")
    @Operation(summary = "Process a payment", description = "Processes a payment and auto-generates an invoice")
    public ResponseEntity<PaymentResponse> processPayment(
            @Valid @RequestBody PaymentRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        if (request.getUserId() == null && userId != null && !userId.isEmpty()) {
            request.setUserId(Long.parseLong(userId));
        }
        PaymentResponse response = paymentService.processPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/my")
    @Operation(summary = "Get current user's payment history", description = "Returns all payments for the authenticated user")
    public ResponseEntity<List<PaymentResponse>> getMyPayments(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        Long effectiveUserId = (userId != null && !userId.isEmpty()) ? Long.parseLong(userId) : 1L;
        return ResponseEntity.ok(paymentService.getHistoryForUser(effectiveUserId));
    }

    @GetMapping("/history")
    @Operation(summary = "Get payment history", description = "Returns all payments for a given user")
    public ResponseEntity<List<PaymentResponse>> getHistory(@RequestParam Long userId) {
        return ResponseEntity.ok(paymentService.getHistoryForUser(userId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID", description = "Returns a single payment by its ID")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @GetMapping("/{id}/invoice")
    @Operation(summary = "Get invoice for payment", description = "Returns the invoice associated with a payment")
    public ResponseEntity<InvoiceResponse> getInvoice(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getInvoiceForPayment(id));
    }
}
