package com.enviro.assessment.junior.ntandomiya.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.enviro.assessment.junior.ntandomiya.entity.Withdrawal;

/**
 * Handles persistence and retrieval of withdrawal records.
 *
 * Withdrawal history is stored rather than overwritten so that the
 * application can later display transaction history and generate CSV reports.
 */
public interface WithdrawalRepository extends JpaRepository<Withdrawal, Long> {

    /**
     * Retrieves the withdrawal history for a specific investor.
     */
    List<Withdrawal> findByInvestorId(Long investorId);
}