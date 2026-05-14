package com.spring.bank.pix.dto;

import com.spring.bank.pix.enums.PixKeyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreatePixKeyDTO(
        @NotNull Long accountId,
        @NotNull PixKeyType type,
        @NotBlank String key
) {}
