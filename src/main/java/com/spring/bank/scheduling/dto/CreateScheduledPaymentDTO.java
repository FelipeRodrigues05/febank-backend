package com.spring.bank.scheduling.dto;

import com.spring.bank.scheduling.enums.ScheduledPaymentType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateScheduledPaymentDTO(
        @NotNull Long fromAccountId,
        @NotNull ScheduledPaymentType type,
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotBlank String targetIdentifier,
        String description,
        @NotNull LocalDate scheduledDate
) {}
