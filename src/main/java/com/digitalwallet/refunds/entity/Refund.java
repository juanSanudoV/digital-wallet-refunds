package com.digitalwallet.refunds.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("refunds")
public class Refund {

    @Id
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