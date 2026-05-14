package com.spring.bank.boleto.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EmitBoletoDTO(
        @NotNull Long issuerAccountId,
        String payerDocument,
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotBlank String description,
        @NotNull LocalDate dueDate
) {}
