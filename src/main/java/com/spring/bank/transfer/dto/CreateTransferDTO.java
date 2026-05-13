package com.spring.bank.transfer.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateTransferDTO(
        @NotNull Long fromAccount,
        @NotNull Long toAccount,
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        String description
) {}
