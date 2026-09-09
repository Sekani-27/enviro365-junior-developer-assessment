package com.enviro.assessment.junior.ntandomiya.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.enviro.assessment.junior.ntandomiya.dto.PortfolioResponse;
import com.enviro.assessment.junior.ntandomiya.service.PortfolioService;

/**
 * Handles HTTP requests related to investor portfolios.
 *
 * The controller only manages the HTTP boundary and delegates application
 * logic to PortfolioService.
 */
@RestController
@RequestMapping("/api/investors")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping("/{investorId}/portfolio")
    public PortfolioResponse getPortfolio(
            @PathVariable Long investorId) {

        return portfolioService.getPortfolioByInvestorId(investorId);
    }
}