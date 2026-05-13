package com.spring.bank.pix.dto;

import com.spring.bank.pix.enums.PixSaqueType;
import com.spring.bank.pix.model.PixSaque;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PixSaqueResponseDTO(
        Long id,
        Long accountId,
        PixSaqueType type,
        BigDecimal totalAmount,
        BigDecimal withdrawalAmount,
        LocalDateTime createdAt
) {
    public PixSaqueResponseDTO(PixSaque pixSaque) {
        this(
                pixSaque.getId(),
                pixSaque.getAccount().getId(),
                pixSaque.getType(),
                pixSaque.getTotalAmount(),
                pixSaque.getWithdrawalAmount(),
                pixSaque.getCreatedAt()
        );
    }
}
