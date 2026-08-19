package com.hotel.payment_service.service;

import com.hotel.payment_service.dto.InvoiceResponse;
import com.hotel.payment_service.dto.PaymentRequest;
import com.hotel.payment_service.dto.PaymentResponse;

import java.util.List;

public interface PaymentService {
    PaymentResponse processPayment(PaymentRequest request);
    List<PaymentResponse> getHistoryForUser(Long userId);
    PaymentResponse getPaymentById(Long id);
    InvoiceResponse getInvoiceForPayment(Long paymentId);
}
