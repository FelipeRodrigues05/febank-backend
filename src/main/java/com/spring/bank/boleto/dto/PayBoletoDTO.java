package com.spring.bank.boleto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PayBoletoDTO(
        @NotNull Long payerAccountId,
        @NotBlank String boletoCode
) {}
