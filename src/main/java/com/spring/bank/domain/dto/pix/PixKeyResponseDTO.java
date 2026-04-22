package com.spring.bank.domain.dto.pix;

import com.spring.bank.domain.enums.pix.PixKeyType;
import com.spring.bank.domain.model.PixKey;

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
