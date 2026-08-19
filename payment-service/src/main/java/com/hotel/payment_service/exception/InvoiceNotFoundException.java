package com.hotel.payment_service.exception;

public class InvoiceNotFoundException extends RuntimeException {
    public InvoiceNotFoundException(Long paymentId) {
        super("Invoice not found for payment id: " + paymentId);
    }
}
