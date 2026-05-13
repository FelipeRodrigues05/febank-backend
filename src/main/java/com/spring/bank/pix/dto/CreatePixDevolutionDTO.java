package com.spring.bank.pix.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreatePixDevolutionDTO(
        @NotNull Long originalTransferId,
        @NotNull Long requesterAccountId,
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotBlank String reason
) {}
