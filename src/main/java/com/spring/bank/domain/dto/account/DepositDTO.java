package com.spring.bank.domain.dto.account;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DepositDTO(
        @NotNull
        Long userId,

        @NotNull
        BigDecimal amount
) {
}
