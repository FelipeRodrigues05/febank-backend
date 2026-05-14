package com.spring.bank.domain.dto.savingsbox;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record SavingsBoxWithdrawDTO(
        @NotNull @Positive(message = "Amount must be greater than zero") BigDecimal amount
) {
}
