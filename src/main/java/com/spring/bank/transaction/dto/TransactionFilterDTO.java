package com.spring.bank.transaction.dto;

import com.spring.bank.transaction.enums.TransactionStatusEnum;
import com.spring.bank.transaction.enums.TransactionTypeEnum;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionFilterDTO(
        Long accountId,
        TransactionTypeEnum type,
        TransactionStatusEnum status,
        LocalDate from,
        LocalDate to,
        BigDecimal minAmount,
        BigDecimal maxAmount,
        String description
) {}
