package com.spring.bank.card.dto;

import com.spring.bank.card.enums.CardType;
import jakarta.validation.constraints.NotNull;

public record CreateCardDTO(
        @NotNull Long accountId,
        @NotNull CardType cardType
) {}
