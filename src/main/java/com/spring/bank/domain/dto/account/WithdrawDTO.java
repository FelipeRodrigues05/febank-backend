package com.spring.bank.domain.dto.account;

import org.springframework.lang.NonNull;

import java.math.BigDecimal;

public record WithdrawDTO(

        @NonNull
        Long accountId,

        @NonNull
        BigDecimal amount
) {
}
