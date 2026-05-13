package com.spring.bank.card.dto;

import java.math.BigDecimal;

public record CashbackSummaryDTO(
        String cardId,
        BigDecimal pendingCashback,
        BigDecimal totalRecords
) {}
