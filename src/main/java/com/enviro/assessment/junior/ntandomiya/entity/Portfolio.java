package com.enviro.assessment.junior.ntandomiya.entity;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

/**
 * Represents the investment portfolio owned by an investor.
 *
 * The portfolio stores the total available balance and is linked
 * one-to-one with an Investor.
 *
 * BigDecimal is used for monetary values to avoid floating-point
 * precision problems when performing financial calculations.
 */

@Entity
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal balance;

    // One portfolio belongs to exactly one investor.
// The investor_id foreign key creates that relationship in the database.

    @OneToOne
    @JoinColumn(name = "investor_id", nullable = false, unique = true)
    private Investor investor;

    public Portfolio() {
    }

    public Portfolio(BigDecimal balance, Investor investor) {
        this.balance = balance;
        this.investor = investor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Investor getInvestor() {
        return investor;
    }

    public void setInvestor(Investor investor) {
        this.investor = investor;
    }
}