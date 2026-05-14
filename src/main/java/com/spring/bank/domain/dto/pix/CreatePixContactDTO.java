package com.spring.bank.domain.dto.pix;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreatePixContactDTO(
        @NotNull Long accountId,
        @NotBlank String alias,
        @NotBlank String pixKey
) {}
