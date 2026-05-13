package com.spring.bank.savings.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateSavingsBoxDTO(
        @NotNull Long accountId,
        @NotBlank String name,
        @NotBlank String imageUrl,
        BigDecimal initialAmount
) {}
