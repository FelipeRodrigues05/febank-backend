package com.spring.bank.openfinance.dto;

import com.spring.bank.openfinance.enums.ConsentStatus;
import com.spring.bank.openfinance.model.Consent;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public record ConsentResponseDTO(
        String id,
        Long userId,
        String clientId,
        List<String> permissions,
        ConsentStatus status,
        LocalDateTime expiresAt,
        LocalDateTime authorisedAt,
        LocalDateTime createdAt
) {
    public ConsentResponseDTO(Consent consent) {
        this(
                consent.getId(),
                consent.getUserId(),
                consent.getClientId(),
                Arrays.asList(consent.getPermissions().split(",")),
                consent.getStatus(),
                consent.getExpiresAt(),
                consent.getAuthorisedAt(),
                consent.getCreatedAt()
        );
    }
}
