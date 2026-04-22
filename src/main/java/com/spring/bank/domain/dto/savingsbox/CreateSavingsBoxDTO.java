package com.spring.bank.domain.dto.savingsbox;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateSavingsBoxDTO(
        @NotNull Long accountId,
        @NotBlank(message = "Name is required") String name,
        @NotBlank(message = "Image URL is required") String imageUrl,
        @DecimalMin(value = "0.01", message = "Initial amount must be greater than zero") BigDecimal initialAmount
) {
}
