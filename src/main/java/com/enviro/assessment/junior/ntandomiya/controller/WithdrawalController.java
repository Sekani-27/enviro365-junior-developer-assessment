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

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.enviro.assessment.junior.ntandomiya.dto.WithdrawalHistoryResponse;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

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

    @GetMapping
    public List<WithdrawalHistoryResponse> getWithdrawalHistory(
        @RequestParam Long investorId) {

    return withdrawalService.getWithdrawalHistory(investorId);
}

@GetMapping("/export")
public ResponseEntity<String> exportWithdrawals(
        @RequestParam Long investorId,

        @RequestParam(required = false)
        Long productId,

        @RequestParam(required = false)
        String status,

        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate from,

        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate to) {

    String csv = withdrawalService.exportWithdrawalsCsv(
            investorId,
            productId,
            status,
            from,
            to);

    return ResponseEntity.ok()
            .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=withdrawals-" + investorId + ".csv")
            .contentType(MediaType.parseMediaType("text/csv"))
            .body(csv);
}
}