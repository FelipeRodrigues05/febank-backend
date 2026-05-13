package com.spring.bank.card.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateChargebackDTO(
        @NotBlank String cardId,
        @NotNull Long transactionId,
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotBlank String reason
) {}
