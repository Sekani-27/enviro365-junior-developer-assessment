package com.enviro.assessment.junior.ntandomiya.config;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.enviro.assessment.junior.ntandomiya.entity.Investor;
import com.enviro.assessment.junior.ntandomiya.entity.Portfolio;
import com.enviro.assessment.junior.ntandomiya.entity.Product;
import com.enviro.assessment.junior.ntandomiya.repository.InvestorRepository;
import com.enviro.assessment.junior.ntandomiya.repository.PortfolioRepository;
import com.enviro.assessment.junior.ntandomiya.repository.ProductRepository;

/**
 * Loads sample data when the application starts.
 *
 * Because H2 is configured as an in-memory database, all data disappears
 * whenever the application stops. This initializer gives us predictable
 * test data every time the system starts.
 */
@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner loadData(
            InvestorRepository investorRepository,
            PortfolioRepository portfolioRepository,
            ProductRepository productRepository) {

        return args -> {

            // Create the investor first because the portfolio must reference
            // an existing investor through its foreign key.
            Investor investor = new Investor(
                    "Thabo",
                    "Mokoena",
                    70);

            investorRepository.save(investor);

            // BigDecimal is used for money to preserve decimal precision.
            Portfolio portfolio = new Portfolio(
                    new BigDecimal("300000.00"),
                    investor);

            portfolioRepository.save(portfolio);

            // One portfolio may contain multiple investment products.
            Product retirementProduct = new Product(
                    "Retirement Fund",
                    "RETIREMENT",
                    new BigDecimal("200000.00"),
                    portfolio);

            Product standardProduct = new Product(
                    "Investment Account",
                    "STANDARD",
                    new BigDecimal("100000.00"),
                    portfolio);

            productRepository.save(retirementProduct);
            productRepository.save(standardProduct);
        };
    }
}