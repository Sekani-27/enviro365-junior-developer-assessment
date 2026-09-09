package com.enviro.assessment.junior.ntandomiya.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Represents the information required to request a withdrawal.
 *
 * Basic request validation is handled here while business-specific
 * withdrawal rules are handled by the service layer.
 */
public class WithdrawalRequest {

    @NotNull(message = "Investor ID is required")
    private Long investorId;

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Withdrawal amount is required")
    @Positive(message = "Withdrawal amount must be greater than zero")
    private BigDecimal amount;

    public WithdrawalRequest() {
    }

    public Long getInvestorId() {
        return investorId;
    }

    public void setInvestorId(Long investorId) {
        this.investorId = investorId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}