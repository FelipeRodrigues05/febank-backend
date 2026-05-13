package com.spring.bank.twofa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VerifyOtpDTO(
        @NotNull Long userId,
        @NotBlank String code
) {}
