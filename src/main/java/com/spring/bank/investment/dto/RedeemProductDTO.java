package com.spring.bank.investment.dto;

import jakarta.validation.constraints.NotNull;

public record RedeemProductDTO(
        @NotNull Long positionId,
        @NotNull Long accountId
) {}
