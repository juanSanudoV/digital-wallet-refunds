package com.digitalwallet.refunds.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("refunds")
public class Refund {

    @Id
    @Column("id")
    private Long id;

    @Column("wallet_id")
    private String walletId;

    @Column("external_transaction_id")
    private String externalTransactionId;

    @Column("amount")
    private BigDecimal amount;

    @Column("currency")
    private String currency;

    @Column("reason")
    private String reason;

    @Column("status")
    private String status;

    @Column("created_at")
    private LocalDateTime createdAt;

    public Refund() {
    }

    public Refund(Long id, String walletId, String externalTransactionId, BigDecimal amount, String currency, String reason, String status, LocalDateTime createdAt) {
        this.id = id;
        this.walletId = walletId;
        this.externalTransactionId = externalTransactionId;
        this.amount = amount;
        this.currency = currency;
        this.reason = reason;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    public String getExternalTransactionId() {
        return externalTransactionId;
    }

    public void setExternalTransactionId(String externalTransactionId) {
        this.externalTransactionId = externalTransactionId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}