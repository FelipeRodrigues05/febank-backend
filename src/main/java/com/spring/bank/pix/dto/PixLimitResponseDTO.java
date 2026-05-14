package com.spring.bank.pix.dto;

import com.spring.bank.pix.model.PixLimit;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PixLimitResponseDTO(
        Long accountId,
        BigDecimal dailyLimit,
        BigDecimal singleTransactionLimit,
        BigDecimal nightLimit,
        LocalDateTime updatedAt
) {
    public PixLimitResponseDTO(PixLimit limit) {
        this(
                limit.getAccount().getId(),
                limit.getDailyLimit(),
                limit.getSingleTransactionLimit(),
                limit.getNightLimit(),
                limit.getUpdatedAt()
        );
    }
}
