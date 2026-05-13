package com.spring.bank.pix.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateScheduledPixDTO(
        @NotNull Long fromAccountId,
        @NotBlank String pixKey,
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        String description,
        @NotNull LocalDate scheduledDate
) {}
