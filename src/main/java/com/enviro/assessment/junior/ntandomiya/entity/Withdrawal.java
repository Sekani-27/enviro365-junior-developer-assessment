package com.enviro.assessment.junior.ntandomiya.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * Represents a withdrawal made against an investment product.
 *
 * A withdrawal records the amount, processing status, creation time,
 * investor and product so that the system can maintain withdrawal history
 * and later generate statements.
 */

@Entity
public class Withdrawal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount;

    private String status;

    private LocalDateTime createdAt;

    // An investor can make many withdrawals over time.

    @ManyToOne
    @JoinColumn(name = "investor_id", nullable = false)
    private Investor investor;

    // A product can have many withdrawals over time.

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public Withdrawal() {
    }

    public Withdrawal(
            BigDecimal amount,
            String status,
            LocalDateTime createdAt,
            Investor investor,
            Product product) {

        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
        this.investor = investor;
        this.product = product;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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

    public Investor getInvestor() {
        return investor;
    }

    public void setInvestor(Investor investor) {
        this.investor = investor;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}