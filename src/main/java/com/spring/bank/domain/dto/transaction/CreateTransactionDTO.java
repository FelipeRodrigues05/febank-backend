package com.spring.bank.domain.dto.transaction;

import com.spring.bank.domain.enums.transaction.TransactionTypeEnum;
import com.spring.bank.domain.model.Account;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;

public record CreateTransactionDTO(
        @NotNull
        Account account,

        @NotNull
        TransactionTypeEnum type,

        @NotNull
        BigDecimal amount,
        String description
) {
}
