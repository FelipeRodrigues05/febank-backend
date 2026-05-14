package com.spring.bank.pix.dto;

import com.spring.bank.pix.enums.ScheduledPixStatus;
import com.spring.bank.pix.model.ScheduledPix;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ScheduledPixResponseDTO(
        Long id,
        Long fromAccountId,
        String pixKey,
        BigDecimal amount,
        String description,
        LocalDate scheduledDate,
        ScheduledPixStatus status,
        LocalDateTime executedAt
) {
    public ScheduledPixResponseDTO(ScheduledPix scheduledPix) {
        this(
                scheduledPix.getId(),
                scheduledPix.getFromAccount().getId(),
                scheduledPix.getPixKey(),
                scheduledPix.getAmount(),
                scheduledPix.getDescription(),
                scheduledPix.getScheduledDate(),
                scheduledPix.getStatus(),
                scheduledPix.getExecutedAt()
        );
    }
}
