package com.spring.bank.domain.dto.account;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record WithdrawDTO(
        @NotNull
        Long userId,

        @NotNull
        BigDecimal amount
) {
}
