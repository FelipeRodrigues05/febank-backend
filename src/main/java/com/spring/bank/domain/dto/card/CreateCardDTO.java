package com.spring.bank.domain.dto.card;

import com.spring.bank.domain.enums.card.CardType;
import jakarta.validation.constraints.NotNull;

public record CreateCardDTO(
        @NotNull Long accountId,
        @NotNull CardType cardType
) {}
