package com.spring.bank.openfinance.dto;

import com.spring.bank.openfinance.enums.ConsentPermission;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record CreateConsentDTO(
        @NotNull Long userId,
        @NotBlank String clientId,
        @NotEmpty List<ConsentPermission> permissions,
        @NotNull LocalDateTime expiresAt
) {}
