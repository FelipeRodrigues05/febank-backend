package com.spring.bank.card.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RequestLimitIncreaseDTO(
        @NotBlank String cardId,
        @NotNull @DecimalMin("100.00") BigDecimal requestedLimit
) {}
