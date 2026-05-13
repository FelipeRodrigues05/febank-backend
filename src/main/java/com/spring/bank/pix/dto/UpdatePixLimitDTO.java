package com.spring.bank.pix.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdatePixLimitDTO(
        @NotNull @DecimalMin("0.01") BigDecimal dailyLimit,
        @NotNull @DecimalMin("0.01") BigDecimal singleTransactionLimit,
        @NotNull @DecimalMin("0.01") BigDecimal nightLimit
) {}
