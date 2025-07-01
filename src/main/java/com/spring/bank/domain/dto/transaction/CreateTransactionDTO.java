package com.spring.bank.domain.dto.transaction;

import com.spring.bank.domain.enums.transaction.TransactionTypeEnum;
import com.spring.bank.domain.model.Account;

import java.math.BigDecimal;

public record CreateTransactionDTO(
        Account account,
        TransactionTypeEnum type,
        BigDecimal amount,
        String description
) {
}
