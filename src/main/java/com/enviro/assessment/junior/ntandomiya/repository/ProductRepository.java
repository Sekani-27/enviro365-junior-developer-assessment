package com.enviro.assessment.junior.ntandomiya.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.enviro.assessment.junior.ntandomiya.entity.Product;

/**
 * Provides database access for investment products.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Retrieves all products belonging to a portfolio.
     *
     * One portfolio can contain many products, therefore a List is returned.
     */
    List<Product> findByPortfolioId(Long portfolioId);
}