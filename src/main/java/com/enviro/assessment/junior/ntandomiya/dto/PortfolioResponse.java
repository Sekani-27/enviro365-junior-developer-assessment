package com.enviro.assessment.junior.ntandomiya.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Represents the portfolio information returned to API clients.
 *
 * This DTO combines investor, portfolio and product information into
 * a response designed specifically for the frontend.
 */
public class PortfolioResponse {

    private Long investorId;
    private String investorName;
    private Integer age;
    private BigDecimal balance;
    private List<ProductResponse> products;

    public PortfolioResponse(
            Long investorId,
            String investorName,
            Integer age,
            BigDecimal balance,
            List<ProductResponse> products) {

        this.investorId = investorId;
        this.investorName = investorName;
        this.age = age;
        this.balance = balance;
        this.products = products;
    }

    public Long getInvestorId() {
        return investorId;
    }

    public String getInvestorName() {
        return investorName;
    }

    public Integer getAge() {
        return age;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public List<ProductResponse> getProducts() {
        return products;
    }
}