package com.digitalwallet.refunds.exception;

public class RefundNotFoundException extends RuntimeException {
    public RefundNotFoundException(Long id) {
        super("Reembolso no encontrado con el ID: " + id);
    }
}