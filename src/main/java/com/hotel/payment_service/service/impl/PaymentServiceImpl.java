package com.hotel.payment_service.service.impl;

import com.hotel.payment_service.dto.InvoiceResponse;
import com.hotel.payment_service.dto.PaymentRequest;
import com.hotel.payment_service.dto.PaymentResponse;
import com.hotel.payment_service.entity.Invoice;
import com.hotel.payment_service.entity.Payment;
import com.hotel.payment_service.entity.PaymentMethod;
import com.hotel.payment_service.entity.PaymentStatus;
import com.hotel.payment_service.exception.InvoiceNotFoundException;
import com.hotel.payment_service.exception.PaymentNotFoundException;
import com.hotel.payment_service.repository.InvoiceRepository;
import com.hotel.payment_service.repository.PaymentRepository;
import com.hotel.payment_service.service.PaymentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository, InvoiceRepository invoiceRepository) {
        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        Payment payment = new Payment();
        payment.setBookingId(request.getBookingId());
        payment.setUserId(request.getUserId());
        payment.setAmount(request.getAmount());
        payment.setCurrency(request.getCurrency());
        payment.setPaymentMethod(PaymentMethod.valueOf(request.getPaymentMethod()));
        payment.setStatus(PaymentStatus.PENDING);

        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setTransactionRef(UUID.randomUUID().toString());

        payment = paymentRepository.save(payment);

        Invoice invoice = new Invoice();
        invoice.setPayment(payment);
        invoice.setInvoiceNumber("INV-" + String.format("%06d", payment.getId()));
        invoice.setAmount(payment.getAmount());
        invoiceRepository.save(invoice);

        return toPaymentResponse(payment);
    }

    @Override
    public List<PaymentResponse> getHistoryForUser(Long userId) {
        return paymentRepository.findByUserId(userId)
                .stream()
                .map(this::toPaymentResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));
        return toPaymentResponse(payment);
    }

    @Override
    public InvoiceResponse getInvoiceForPayment(Long paymentId) {
        Invoice invoice = invoiceRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new InvoiceNotFoundException(paymentId));
        return toInvoiceResponse(invoice);
    }

    private PaymentResponse toPaymentResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setBookingId(payment.getBookingId());
        response.setUserId(payment.getUserId());
        response.setAmount(payment.getAmount());
        response.setCurrency(payment.getCurrency());
        response.setStatus(payment.getStatus().name());
        response.setPaymentMethod(payment.getPaymentMethod().name());
        response.setTransactionRef(payment.getTransactionRef());
        response.setCreatedAt(payment.getCreatedAt());
        return response;
    }

    private InvoiceResponse toInvoiceResponse(Invoice invoice) {
        InvoiceResponse response = new InvoiceResponse();
        response.setId(invoice.getId());
        response.setPaymentId(invoice.getPayment().getId());
        response.setInvoiceNumber(invoice.getInvoiceNumber());
        response.setAmount(invoice.getAmount());
        response.setIssuedAt(invoice.getIssuedAt());
        return response;
    }
}
