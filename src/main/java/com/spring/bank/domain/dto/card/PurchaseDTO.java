package com.spring.bank.domain.dto.card;

import com.spring.bank.domain.enums.card.CardType;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PurchaseDTO(
        @NotNull
        String cardNumber,

        @NotNull
        String cvv,

        @NotNull
        BigDecimal amount,

        @NotNull
        CardType cardType
) {
}
