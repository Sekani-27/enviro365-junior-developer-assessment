package com.enviro.assessment.junior.ntandomiya.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.enviro.assessment.junior.ntandomiya.dto.WithdrawalRequest;
import com.enviro.assessment.junior.ntandomiya.dto.WithdrawalResponse;
import com.enviro.assessment.junior.ntandomiya.service.WithdrawalService;

import jakarta.validation.Valid;

/**
 * Handles HTTP requests for withdrawal processing.
 *
 * The controller validates incoming request data and delegates
 * business decisions to WithdrawalService.
 */
@RestController
@RequestMapping("/api/withdrawals")
public class WithdrawalController {

    private final WithdrawalService withdrawalService;

    public WithdrawalController(WithdrawalService withdrawalService) {
        this.withdrawalService = withdrawalService;
    }

    @PostMapping
    public ResponseEntity<WithdrawalResponse> createWithdrawal(
            @Valid @RequestBody WithdrawalRequest request) {

        WithdrawalResponse response =
                withdrawalService.createWithdrawal(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}