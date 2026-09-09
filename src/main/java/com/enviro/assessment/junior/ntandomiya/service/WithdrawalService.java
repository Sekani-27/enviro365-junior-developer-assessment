package com.enviro.assessment.junior.ntandomiya.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.enviro.assessment.junior.ntandomiya.dto.WithdrawalRequest;
import com.enviro.assessment.junior.ntandomiya.dto.WithdrawalResponse;
import com.enviro.assessment.junior.ntandomiya.entity.Investor;
import com.enviro.assessment.junior.ntandomiya.entity.Portfolio;
import com.enviro.assessment.junior.ntandomiya.entity.Product;
import com.enviro.assessment.junior.ntandomiya.entity.Withdrawal;
import com.enviro.assessment.junior.ntandomiya.exception.BusinessRuleException;
import com.enviro.assessment.junior.ntandomiya.exception.ResourceNotFoundException;
import com.enviro.assessment.junior.ntandomiya.repository.InvestorRepository;
import com.enviro.assessment.junior.ntandomiya.repository.PortfolioRepository;
import com.enviro.assessment.junior.ntandomiya.repository.ProductRepository;
import com.enviro.assessment.junior.ntandomiya.repository.WithdrawalRepository;

/**
 * Handles the business rules and state changes required for withdrawals.
 *
 * A withdrawal is processed as one transaction so that product balance,
 * portfolio balance and the withdrawal record remain consistent.
 */
@Service
public class WithdrawalService {

    private static final BigDecimal MAX_WITHDRAWAL_PERCENTAGE =
            new BigDecimal("0.90");

    private final InvestorRepository investorRepository;
    private final PortfolioRepository portfolioRepository;
    private final ProductRepository productRepository;
    private final WithdrawalRepository withdrawalRepository;

    public WithdrawalService(
            InvestorRepository investorRepository,
            PortfolioRepository portfolioRepository,
            ProductRepository productRepository,
            WithdrawalRepository withdrawalRepository) {

        this.investorRepository = investorRepository;
        this.portfolioRepository = portfolioRepository;
        this.productRepository = productRepository;
        this.withdrawalRepository = withdrawalRepository;
    }

    @Transactional
    public WithdrawalResponse createWithdrawal(WithdrawalRequest request) {

        Investor investor = investorRepository
                .findById(request.getInvestorId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Investor not found: " + request.getInvestorId()));

        Product product = productRepository
                .findById(request.getProductId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found: " + request.getProductId()));

        Portfolio portfolio = product.getPortfolio();

        // Prevent an investor from withdrawing from another investor's product.
        if (!portfolio.getInvestor().getId().equals(investor.getId())) {
            throw new BusinessRuleException(
                    "The selected product does not belong to this investor");
        }

        /*
         * Retirement products have an additional eligibility rule:
         * the investor must be older than 65.
         */
        if ("RETIREMENT".equalsIgnoreCase(product.getType())
                && investor.getAge() <= 65) {

            throw new BusinessRuleException(
                    "Retirement withdrawals are only allowed for investors older than 65");
        }

        BigDecimal amount = request.getAmount();
        BigDecimal availableBalance = product.getProductValue();

        // A withdrawal cannot exceed the selected product's available balance.
        if (amount.compareTo(availableBalance) > 0) {
            throw new BusinessRuleException(
                    "Withdrawal amount exceeds the available balance");
        }

        BigDecimal maximumAllowed =
                availableBalance.multiply(MAX_WITHDRAWAL_PERCENTAGE);

        // No single withdrawal may exceed 90% of the available product balance.
        if (amount.compareTo(maximumAllowed) > 0) {
            throw new BusinessRuleException(
                    "Withdrawal amount cannot exceed 90% of the available balance");
        }

        // Defensive check to keep the overall portfolio balance consistent.
        if (amount.compareTo(portfolio.getBalance()) > 0) {
            throw new BusinessRuleException(
                    "Withdrawal amount exceeds the portfolio balance");
        }

        BigDecimal remainingProductValue =
                availableBalance.subtract(amount);

        BigDecimal remainingPortfolioBalance =
                portfolio.getBalance().subtract(amount);

        product.setProductValue(remainingProductValue);
        portfolio.setBalance(remainingPortfolioBalance);

        productRepository.save(product);
        portfolioRepository.save(portfolio);

        Withdrawal withdrawal = new Withdrawal(
                amount,
                "APPROVED",
                LocalDateTime.now(),
                investor,
                product);

        Withdrawal savedWithdrawal =
                withdrawalRepository.save(withdrawal);

        return new WithdrawalResponse(
                savedWithdrawal.getId(),
                savedWithdrawal.getStatus(),
                savedWithdrawal.getAmount(),
                remainingProductValue,
                remainingPortfolioBalance,
                savedWithdrawal.getCreatedAt());
    }
}