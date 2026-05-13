package com.spring.bank.pix.dto;

import com.spring.bank.pix.model.PixContact;

import java.time.LocalDateTime;

public record PixContactResponseDTO(
        Long id,
        String alias,
        String pixKey,
        int transferCount,
        LocalDateTime createdAt
) {
    public PixContactResponseDTO(PixContact contact) {
        this(
                contact.getId(),
                contact.getAlias(),
                contact.getPixKey(),
                contact.getTransferCount(),
                contact.getCreatedAt()
        );
    }
}
