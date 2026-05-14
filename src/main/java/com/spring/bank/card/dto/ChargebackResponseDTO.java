package com.spring.bank.card.dto;

import com.spring.bank.card.enums.ChargebackStatus;
import com.spring.bank.card.model.Chargeback;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ChargebackResponseDTO(
        Long id,
        String cardId,
        Long transactionId,
        BigDecimal amount,
        String reason,
        ChargebackStatus status,
        LocalDateTime createdAt
) {
    public ChargebackResponseDTO(Chargeback chargeback) {
        this(
                chargeback.getId(),
                chargeback.getCard().getId(),
                chargeback.getOriginalTransactionId(),
                chargeback.getAmount(),
                chargeback.getReason(),
                chargeback.getStatus(),
                chargeback.getCreatedAt()
        );
    }
}
