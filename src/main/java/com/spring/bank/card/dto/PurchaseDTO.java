package com.spring.bank.card.dto;

import com.spring.bank.card.enums.CardType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PurchaseDTO(
        @NotBlank String cardNumber,
        @NotBlank String cvv,
        @NotNull CardType cardType,
        @NotNull @DecimalMin("0.01") BigDecimal amount
) {}
