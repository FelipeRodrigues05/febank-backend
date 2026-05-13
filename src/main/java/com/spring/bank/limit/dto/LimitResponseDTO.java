package com.spring.bank.limit.dto;

import com.spring.bank.limit.enums.LimitType;
import com.spring.bank.limit.model.OperationLimit;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LimitResponseDTO(
        LimitType limitType,
        BigDecimal maxAmount,
        LocalDateTime updatedAt
) {
    public LimitResponseDTO(OperationLimit limit) {
        this(limit.getLimitType(), limit.getMaxAmount(), limit.getUpdatedAt());
    }
}
