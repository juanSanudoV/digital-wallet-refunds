package com.digitalwallet.refunds.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RefundEvent(
        Long refundId,
        String walletId,
        BigDecimal amount,
        String status,
        String externalTransactionId,
        LocalDateTime eventTimestamp
) {}