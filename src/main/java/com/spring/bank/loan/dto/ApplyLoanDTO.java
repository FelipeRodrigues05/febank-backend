package com.spring.bank.loan.dto;

import com.spring.bank.loan.enums.LoanType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ApplyLoanDTO(
        @NotNull Long accountId,
        @NotNull LoanType type,
        @NotNull @DecimalMin("500.00") BigDecimal requestedAmount,
        @NotNull @Min(3) @Max(60) int totalInstallments
) {}
