package com.spring.bank.domain.dto.pix;

import com.spring.bank.domain.enums.pix.PixKeyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreatePixKeyDTO(
        @NotNull Long accountId,
        @NotNull PixKeyType type,
        @NotBlank(message = "PIX key value is required") String key
) {
}
