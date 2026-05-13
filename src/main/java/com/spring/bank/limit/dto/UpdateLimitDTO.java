package com.spring.bank.limit.dto;

import com.spring.bank.limit.enums.LimitType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateLimitDTO(
        @NotNull LimitType limitType,
        @NotNull @DecimalMin("0.01") BigDecimal maxAmount
) {}
