package com.spring.bank.domain.dto.account;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DepositDTO(
        @NotNull
        Long accountId,

        @NotNull
        BigDecimal amount
) {
}
