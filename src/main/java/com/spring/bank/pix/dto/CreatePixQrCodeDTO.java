package com.spring.bank.pix.dto;

import com.spring.bank.pix.enums.PixQrCodeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreatePixQrCodeDTO(
        @NotNull Long accountId,
        @NotBlank String pixKey,
        PixQrCodeType type,
        BigDecimal amount,
        String description,
        Integer expiryMinutes
) {}
