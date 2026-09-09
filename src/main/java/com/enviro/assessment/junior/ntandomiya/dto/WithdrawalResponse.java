package com.enviro.assessment.junior.ntandomiya.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Defines the data returned after a withdrawal is processed.
 */
public class WithdrawalResponse {

    private Long withdrawalId;
    private String status;
    private BigDecimal amount;
    private BigDecimal remainingProductValue;
    private BigDecimal remainingPortfolioBalance;
    private LocalDateTime createdAt;

    public WithdrawalResponse(
            Long withdrawalId,
            String status,
            BigDecimal amount,
            BigDecimal remainingProductValue,
            BigDecimal remainingPortfolioBalance,
            LocalDateTime createdAt) {

        this.withdrawalId = withdrawalId;
        this.status = status;
        this.amount = amount;
        this.remainingProductValue = remainingProductValue;
        this.remainingPortfolioBalance = remainingPortfolioBalance;
        this.createdAt = createdAt;
    }

    public Long getWithdrawalId() {
        return withdrawalId;
    }

    public String getStatus() {
        return status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getRemainingProductValue() {
        return remainingProductValue;
    }

    public BigDecimal getRemainingPortfolioBalance() {
        return remainingPortfolioBalance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}