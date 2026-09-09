package com.enviro.assessment.junior.ntandomiya.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a withdrawal entry displayed in withdrawal history.
 */
public class WithdrawalHistoryResponse {

    private Long withdrawalId;
    private Long productId;
    private String productName;
    private BigDecimal amount;
    private String status;
    private LocalDateTime createdAt;

    public WithdrawalHistoryResponse(
            Long withdrawalId,
            Long productId,
            String productName,
            BigDecimal amount,
            String status,
            LocalDateTime createdAt) {

        this.withdrawalId = withdrawalId;
        this.productId = productId;
        this.productName = productName;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getWithdrawalId() {
        return withdrawalId;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}