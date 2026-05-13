package com.spring.bank.investment.dto;

import com.spring.bank.account.model.Account;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InvestmentResponseDTO(
        Long accountId,
        BigDecimal balance,
        LocalDateTime updatedAt
) {
    public InvestmentResponseDTO(Account account) {
        this(account.getId(), account.getBalance(), account.getUpdatedAt());
    }
}
