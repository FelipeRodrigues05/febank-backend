package com.spring.bank.card.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record InstallmentPurchaseDTO(
        @NotBlank String cardNumber,
        @NotBlank String cvv,
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotBlank String description,
        @NotNull @Min(2) @Max(12) int installments
) {}
