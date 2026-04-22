package com.spring.bank.domain.dto.investment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record RedeemInvestmentDTO(
        @NotNull Long userId,
        @NotNull @Positive(message = "Redemption amount must be greater than zero") BigDecimal amount
) {
}
