package com.spring.bank.card.dto;

import com.spring.bank.card.model.VirtualCard;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record VirtualCardResponseDTO(
        String cardId,
        String maskedNumber,
        LocalDate expiresAt,
        boolean active,
        LocalDateTime createdAt
) {
    public VirtualCardResponseDTO(VirtualCard virtualCard) {
        this(
                virtualCard.getCard().getId(),
                maskCardNumber(virtualCard.getCard().getNumber()),
                virtualCard.getExpiresAt(),
                virtualCard.isActive(),
                virtualCard.getCreatedAt()
        );
    }

    private static String maskCardNumber(String number) {
        if (number == null || number.length() < 4) return "****";
        return "**** **** **** " + number.substring(number.length() - 4);
    }
}
