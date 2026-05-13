package com.spring.bank.transaction.dto;

import com.spring.bank.account.model.Account;
import com.spring.bank.transaction.enums.TransactionTypeEnum;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateTransactionDTO(
        @NotNull Account account,
        @NotNull TransactionTypeEnum type,
        @NotNull BigDecimal amount,
        String description
) {}
