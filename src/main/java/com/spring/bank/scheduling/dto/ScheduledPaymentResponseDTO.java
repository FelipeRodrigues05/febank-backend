package com.spring.bank.scheduling.dto;

import com.spring.bank.scheduling.enums.ScheduledPaymentStatus;
import com.spring.bank.scheduling.enums.ScheduledPaymentType;
import com.spring.bank.scheduling.model.ScheduledPayment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ScheduledPaymentResponseDTO(
        Long id,
        Long fromAccountId,
        ScheduledPaymentType type,
        BigDecimal amount,
        String targetIdentifier,
        String description,
        LocalDate scheduledDate,
        ScheduledPaymentStatus status,
        LocalDateTime executedAt
) {
    public ScheduledPaymentResponseDTO(ScheduledPayment payment) {
        this(
                payment.getId(),
                payment.getFromAccountId(),
                payment.getType(),
                payment.getAmount(),
                payment.getTargetIdentifier(),
                payment.getDescription(),
                payment.getScheduledDate(),
                payment.getStatus(),
                payment.getExecutedAt()
        );
    }
}
