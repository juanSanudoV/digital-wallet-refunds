package com.digitalwallet.refunds.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
    int status,
    String message,
    List<String> errors,
    LocalDateTime timestamp
) {
    // Constructor conveniente para no pasar la fecha manualmente
    public ErrorResponse(int status, String message, List<String> errors) {
        this(status, message, errors, LocalDateTime.now());
    }
}