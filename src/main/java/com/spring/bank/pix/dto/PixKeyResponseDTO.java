package com.spring.bank.pix.dto;

import com.spring.bank.pix.enums.PixKeyType;
import com.spring.bank.pix.model.PixKey;

import java.time.LocalDateTime;

public record PixKeyResponseDTO(
        String id,
        Long accountId,
        PixKeyType type,
        String key,
        LocalDateTime createdAt
) {
    public PixKeyResponseDTO(PixKey pixKey) {
        this(
                pixKey.getId(),
                pixKey.getAccount().getId(),
                pixKey.getType(),
                pixKey.getKey(),
                pixKey.getCreatedAt()
        );
    }
}
