package com.spring.bank.domain.dto.investment;

import java.math.BigDecimal;

public record ApplyInvestmentDTO(
        Long userId,
        BigDecimal amount
) {
}
