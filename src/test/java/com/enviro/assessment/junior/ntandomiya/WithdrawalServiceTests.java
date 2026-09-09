package com.enviro.assessment.junior.ntandomiya;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enviro.assessment.junior.ntandomiya.dto.WithdrawalRequest;
import com.enviro.assessment.junior.ntandomiya.dto.WithdrawalResponse;
import com.enviro.assessment.junior.ntandomiya.entity.Investor;
import com.enviro.assessment.junior.ntandomiya.entity.Portfolio;
import com.enviro.assessment.junior.ntandomiya.entity.Product;
import com.enviro.assessment.junior.ntandomiya.entity.Withdrawal;
import com.enviro.assessment.junior.ntandomiya.exception.BusinessRuleException;
import com.enviro.assessment.junior.ntandomiya.repository.InvestorRepository;
import com.enviro.assessment.junior.ntandomiya.repository.PortfolioRepository;
import com.enviro.assessment.junior.ntandomiya.repository.ProductRepository;
import com.enviro.assessment.junior.ntandomiya.repository.WithdrawalRepository;
import com.enviro.assessment.junior.ntandomiya.service.WithdrawalService;

/**
 * Unit tests for the core withdrawal business rules.
 *
 * Repositories are mocked so these tests focus only on service behaviour
 * rather than requiring a real database.
 */
@ExtendWith(MockitoExtension.class)
class WithdrawalServiceTests {

    @Mock
    private InvestorRepository investorRepository;

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private WithdrawalRepository withdrawalRepository;

    private WithdrawalService withdrawalService;

    @BeforeEach
    void setUp() {
        withdrawalService = new WithdrawalService(
                investorRepository,
                portfolioRepository,
                productRepository,
                withdrawalRepository);
    }

    @Test
    void shouldApproveValidRetirementWithdrawal() {

        Investor investor = createInvestor(1L, 70);
        Portfolio portfolio =
                createPortfolio(1L, investor, "300000.00");

        Product product =
                createProduct(
                        1L,
                        portfolio,
                        "RETIREMENT",
                        "200000.00");

        WithdrawalRequest request =
                createRequest(1L, 1L, "50000.00");

        when(investorRepository.findById(1L))
                .thenReturn(Optional.of(investor));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(withdrawalRepository.save(any(Withdrawal.class)))
                .thenAnswer(invocation -> {
                    Withdrawal withdrawal = invocation.getArgument(0);
                    withdrawal.setId(1L);
                    return withdrawal;
                });

        WithdrawalResponse response =
                withdrawalService.createWithdrawal(request);

        assertEquals("APPROVED", response.getStatus());

        assertEquals(
                0,
                new BigDecimal("150000.00")
                        .compareTo(response.getRemainingProductValue()));

        assertEquals(
                0,
                new BigDecimal("250000.00")
                        .compareTo(response.getRemainingPortfolioBalance()));
    }

    @Test
    void shouldRejectRetirementWithdrawalWhenInvestorIsTooYoung() {

        Investor investor = createInvestor(1L, 65);
        Portfolio portfolio =
                createPortfolio(1L, investor, "300000.00");

        Product product =
                createProduct(
                        1L,
                        portfolio,
                        "RETIREMENT",
                        "200000.00");

        WithdrawalRequest request =
                createRequest(1L, 1L, "50000.00");

        when(investorRepository.findById(1L))
                .thenReturn(Optional.of(investor));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        assertThrows(
                BusinessRuleException.class,
                () -> withdrawalService.createWithdrawal(request));
    }

    @Test
    void shouldRejectWithdrawalAboveNinetyPercent() {

        Investor investor = createInvestor(1L, 70);
        Portfolio portfolio =
                createPortfolio(1L, investor, "300000.00");

        Product product =
                createProduct(
                        1L,
                        portfolio,
                        "RETIREMENT",
                        "200000.00");

        WithdrawalRequest request =
                createRequest(1L, 1L, "190000.00");

        when(investorRepository.findById(1L))
                .thenReturn(Optional.of(investor));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        assertThrows(
                BusinessRuleException.class,
                () -> withdrawalService.createWithdrawal(request));
    }

    private Investor createInvestor(Long id, Integer age) {
        Investor investor =
                new Investor("Thabo", "Mokoena", age);

        investor.setId(id);

        return investor;
    }

    private Portfolio createPortfolio(
            Long id,
            Investor investor,
            String balance) {

        Portfolio portfolio =
                new Portfolio(
                        new BigDecimal(balance),
                        investor);

        portfolio.setId(id);

        return portfolio;
    }

    private Product createProduct(
            Long id,
            Portfolio portfolio,
            String type,
            String value) {

        Product product =
                new Product(
                        "Test Product",
                        type,
                        new BigDecimal(value),
                        portfolio);

        product.setId(id);

        return product;
    }

    private WithdrawalRequest createRequest(
            Long investorId,
            Long productId,
            String amount) {

        WithdrawalRequest request =
                new WithdrawalRequest();

        request.setInvestorId(investorId);
        request.setProductId(productId);
        request.setAmount(new BigDecimal(amount));

        return request;
    }
}