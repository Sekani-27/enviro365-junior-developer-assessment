package com.enviro.assessment.junior.ntandomiya.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.enviro.assessment.junior.ntandomiya.entity.Investor;

/**
 * Provides database access operations for Investor entities.
 *
 * By extending JpaRepository, Spring Data JPA automatically provides
 * common operations such as save(), findById(), findAll() and delete().
 *
 * This keeps database access separate from business logic.
 */
public interface InvestorRepository extends JpaRepository<Investor, Long> {
}