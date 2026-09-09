package com.enviro.assessment.junior.ntandomiya.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.enviro.assessment.junior.ntandomiya.entity.Portfolio;

/**
 * Handles database access for Portfolio records.
 */
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    /**
     * Finds the portfolio belonging to a specific investor.
     *
     * Spring Data JPA derives the database query from the method name:
     * find By Investor Id.
     */
    Optional<Portfolio> findByInvestorId(Long investorId);
}