package com.spring.bank.loan.dto;

import jakarta.validation.constraints.NotNull;

public record PayInstallmentDTO(
        @NotNull Long loanId,
        @NotNull Long accountId,
        @NotNull int installmentNumber
) {}
