package com.spring.bank.domain.dto.transfer;

import com.spring.bank.domain.model.Account;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateTransferDTO(
        @NotNull
        Long fromAccount,

        @NotNull
        Long toAccount,

        @NotNull
        BigDecimal amount,

        String description
) {
}
