package com.spring.bank.account.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DepositDTO(
        @NotNull Long userId,
        @NotNull @DecimalMin("0.01") BigDecimal amount
) {}
