package com.spring.bank.pix.dto;

import com.spring.bank.pix.enums.PixSaqueType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreatePixSaqueDTO(
        @NotNull Long accountId,
        @NotNull Long merchantAccountId,
        @NotNull @DecimalMin("0.01") BigDecimal withdrawalAmount,
        PixSaqueType type,
        BigDecimal purchaseAmount
) {}
