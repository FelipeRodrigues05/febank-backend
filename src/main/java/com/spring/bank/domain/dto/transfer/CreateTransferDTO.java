package com.spring.bank.domain.dto.transfer;

import com.spring.bank.domain.model.Account;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateTransferDTO(
        Account fromAccount,
        Account toAccount,
        BigDecimal amount,
        String description
) {
}
