package com.spring.bank.investment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ApplyProductInvestmentDTO(
        @NotNull Long accountId,
        @NotNull Long productId,
        @NotNull @DecimalMin("0.01") BigDecimal amount
) {}
