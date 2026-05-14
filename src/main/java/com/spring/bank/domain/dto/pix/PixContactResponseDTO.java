package com.spring.bank.domain.dto.pix;

import com.spring.bank.domain.model.PixContact;

import java.time.LocalDateTime;

public record PixContactResponseDTO(
        Long id,
        Long accountId,
        String alias,
        String pixKey,
        int transferCount,
        LocalDateTime createdAt
) {
    public PixContactResponseDTO(PixContact contact) {
        this(
                contact.getId(),
                contact.getAccount().getId(),
                contact.getAlias(),
                contact.getPixKey(),
                contact.getTransferCount(),
                contact.getCreatedAt()
        );
    }
}
