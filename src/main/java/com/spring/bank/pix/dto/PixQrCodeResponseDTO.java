package com.spring.bank.pix.dto;

import com.spring.bank.pix.enums.PixQrCodeType;
import com.spring.bank.pix.model.PixQrCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PixQrCodeResponseDTO(
        String id,
        String pixKey,
        PixQrCodeType type,
        BigDecimal amount,
        String description,
        String payload,
        boolean active,
        LocalDateTime expiresAt,
        LocalDateTime createdAt
) {
    public PixQrCodeResponseDTO(PixQrCode qrCode) {
        this(
                qrCode.getId(),
                qrCode.getPixKey(),
                qrCode.getType(),
                qrCode.getAmount(),
                qrCode.getDescription(),
                qrCode.getPayload(),
                qrCode.isActive(),
                qrCode.getExpiresAt(),
                qrCode.getCreatedAt()
        );
    }
}
