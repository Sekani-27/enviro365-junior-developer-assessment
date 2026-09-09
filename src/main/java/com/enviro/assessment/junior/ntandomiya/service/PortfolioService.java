package com.enviro.assessment.junior.ntandomiya.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.enviro.assessment.junior.ntandomiya.dto.PortfolioResponse;
import com.enviro.assessment.junior.ntandomiya.dto.ProductResponse;
import com.enviro.assessment.junior.ntandomiya.entity.Portfolio;
import com.enviro.assessment.junior.ntandomiya.entity.Product;
import com.enviro.assessment.junior.ntandomiya.repository.PortfolioRepository;
import com.enviro.assessment.junior.ntandomiya.repository.ProductRepository;

/**
 * Contains application logic related to investor portfolios.
 *
 * The service coordinates repository access and converts database
 * entities into DTOs before information is returned through the API.
 */
@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final ProductRepository productRepository;

    public PortfolioService(
            PortfolioRepository portfolioRepository,
            ProductRepository productRepository) {

        this.portfolioRepository = portfolioRepository;
        this.productRepository = productRepository;
    }

    public PortfolioResponse getPortfolioByInvestorId(Long investorId) {

        // Retrieve the portfolio associated with the investor.
        Portfolio portfolio = portfolioRepository
                .findByInvestorId(investorId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Portfolio not found for investor: " + investorId));

        // Retrieve every investment product belonging to the portfolio.
        List<Product> products =
                productRepository.findByPortfolioId(portfolio.getId());

        // Convert Product entities into API-safe ProductResponse DTOs.
        List<ProductResponse> productResponses = new ArrayList<>();

        for (Product product : products) {

            ProductResponse response = new ProductResponse(
                    product.getId(),
                    product.getName(),
                    product.getType(),
                    product.getProductValue());

            productResponses.add(response);
        }

        String investorName =
                portfolio.getInvestor().getFirstName()
                        + " "
                        + portfolio.getInvestor().getLastName();

        // Return a DTO rather than exposing JPA entities directly.
        return new PortfolioResponse(
                portfolio.getInvestor().getId(),
                investorName,
                portfolio.getInvestor().getAge(),
                portfolio.getBalance(),
                productResponses);
    }
}