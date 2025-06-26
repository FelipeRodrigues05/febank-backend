package com.spring.bank.domain.dto.account;

import java.math.BigDecimal;
import java.util.UUID;

public record DepositDTO(
        UUID accountId,
        BigDecimal amount
) {
}
