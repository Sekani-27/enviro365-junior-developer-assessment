package com.enviro.assessment.junior.ntandomiya.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.enviro.assessment.junior.ntandomiya.dto.WithdrawalHistoryResponse;
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

import java.time.LocalDate;
/**
 * Handles withdrawal business rules, balance updates and history retrieval.
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

    /**
     * Processes a withdrawal as one transaction so all related
     * database changes either succeed together or roll back together.
     */
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

        // Ensure the selected product belongs to the requesting investor.
        if (!portfolio.getInvestor().getId().equals(investor.getId())) {
            throw new BusinessRuleException(
                    "The selected product does not belong to this investor");
        }

        // Retirement withdrawals require the investor to be older than 65.
        if ("RETIREMENT".equalsIgnoreCase(product.getType())
                && investor.getAge() <= 65) {

            throw new BusinessRuleException(
                    "Retirement withdrawals are only allowed for investors older than 65");
        }

        BigDecimal amount = request.getAmount();
        BigDecimal availableBalance = product.getProductValue();

        if (amount.compareTo(availableBalance) > 0) {
            throw new BusinessRuleException(
                    "Withdrawal amount exceeds the available balance");
        }

        BigDecimal maximumAllowed =
                availableBalance.multiply(MAX_WITHDRAWAL_PERCENTAGE);

        if (amount.compareTo(maximumAllowed) > 0) {
            throw new BusinessRuleException(
                    "Withdrawal amount cannot exceed 90% of the available balance");
        }

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

    /**
     * Retrieves withdrawal history for a specific investor and converts
     * database entities into API response DTOs.
     */
    @Transactional(readOnly = true)
    public List<WithdrawalHistoryResponse> getWithdrawalHistory(
            Long investorId) {

        investorRepository.findById(investorId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Investor not found: " + investorId));

        List<Withdrawal> withdrawals =
                withdrawalRepository.findByInvestorId(investorId);

        return withdrawals.stream()
                .map(withdrawal ->
                        new WithdrawalHistoryResponse(
                                withdrawal.getId(),
                                withdrawal.getProduct().getId(),
                                withdrawal.getProduct().getName(),
                                withdrawal.getAmount(),
                                withdrawal.getStatus(),
                                withdrawal.getCreatedAt()))
                .toList();
    }

    @Transactional(readOnly = true)
    public String exportWithdrawalsCsv(
        Long investorId,
        Long productId,
        String status,
        LocalDate from,
        LocalDate to) {

    investorRepository.findById(investorId)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Investor not found: " + investorId));

    List<Withdrawal> withdrawals =
            withdrawalRepository.findByInvestorId(investorId);

    StringBuilder csv = new StringBuilder();

    csv.append(
            "withdrawalId,productId,productName,amount,status,createdAt\n");

    withdrawals.stream()

            // Optional product filter.
            .filter(withdrawal ->
                    productId == null
                            || withdrawal.getProduct()
                                    .getId()
                                    .equals(productId))

            // Optional status filter.
            .filter(withdrawal ->
                    status == null
                            || withdrawal.getStatus()
                                    .equalsIgnoreCase(status))

            // Optional starting-date filter.
            .filter(withdrawal ->
                    from == null
                            || !withdrawal.getCreatedAt()
                                    .toLocalDate()
                                    .isBefore(from))

            // Optional ending-date filter.
            .filter(withdrawal ->
                    to == null
                            || !withdrawal.getCreatedAt()
                                    .toLocalDate()
                                    .isAfter(to))

            .forEach(withdrawal -> csv.append(
                    withdrawal.getId()).append(",")
                    .append(withdrawal.getProduct().getId()).append(",")
                    .append("\"")
                    .append(withdrawal.getProduct().getName())
                    .append("\"").append(",")
                    .append(withdrawal.getAmount()).append(",")
                    .append(withdrawal.getStatus()).append(",")
                    .append(withdrawal.getCreatedAt())
                    .append("\n"));

    return csv.toString();
}
}