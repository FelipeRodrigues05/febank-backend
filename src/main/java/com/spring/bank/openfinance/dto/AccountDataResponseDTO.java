package com.spring.bank.openfinance.dto;

import java.math.BigDecimal;

public record AccountDataResponseDTO(
        Long accountId,
        String accountNumber,
        String accountType,
        BigDecimal balance,
        String ownerName
) {}
