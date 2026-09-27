package com.digitalwallet.refunds.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundResponse {
    private Long id;
    private String sku;
    private Integer quantity;
    private String walletId;
    private String externalTransactionId;
    private BigDecimal amount;
    private String currency;
    private String reason;
    private String status;
    private LocalDateTime createdAt;
}