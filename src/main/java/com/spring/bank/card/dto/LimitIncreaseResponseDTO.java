package com.spring.bank.card.dto;

import com.spring.bank.card.enums.LimitIncreaseStatus;
import com.spring.bank.card.model.LimitIncreaseRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LimitIncreaseResponseDTO(
        Long id,
        BigDecimal currentLimit,
        BigDecimal requestedLimit,
        LimitIncreaseStatus status,
        LocalDateTime createdAt
) {
    public LimitIncreaseResponseDTO(LimitIncreaseRequest request) {
        this(
                request.getId(),
                request.getCurrentLimit(),
                request.getRequestedLimit(),
                request.getStatus(),
                request.getCreatedAt()
        );
    }
}
