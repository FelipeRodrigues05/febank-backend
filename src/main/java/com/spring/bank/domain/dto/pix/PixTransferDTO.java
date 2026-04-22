package com.spring.bank.domain.dto.pix;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PixTransferDTO(
        @NotNull Long fromAccountId,
        @NotBlank(message = "PIX key is required") String pixKey,
        @NotNull @DecimalMin(value = "0.01", message = "Amount must be greater than zero") BigDecimal amount,
        String description
) {
}
