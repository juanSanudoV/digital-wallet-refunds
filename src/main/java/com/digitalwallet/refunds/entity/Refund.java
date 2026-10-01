package com.digitalwallet.refunds.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("refunds")
public class Refund implements Persistable<Long> {

    @Id
    private Long id;
    private String sku;
    private Integer quantity;

    @Column("wallet_id")
    private String walletId;

    @Column("external_transaction_id")
    private String externalTransactionId;

    private BigDecimal amount;
    private String currency;
    private String reason;
    private String status;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Transient
    private boolean isNewRecord = true;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return isNewRecord || id == null;
    }

    public void setNewRecord(boolean newRecord) {
        this.isNewRecord = newRecord;
    }

    public void setId(Long id) { this.id = id; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public String getWalletId() { return walletId; }
    public void setWalletId(String walletId) { this.walletId = walletId; }
    public String getExternalTransactionId() { return externalTransactionId; }
    public void setExternalTransactionId(String externalTransactionId) { this.externalTransactionId = externalTransactionId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}