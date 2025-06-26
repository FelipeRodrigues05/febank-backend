package com.spring.bank.domain.dto.account;

import java.math.BigDecimal;
import java.util.UUID;

public record WithdrawDTO(
        UUID accountId,
        BigDecimal amount
) {
}
