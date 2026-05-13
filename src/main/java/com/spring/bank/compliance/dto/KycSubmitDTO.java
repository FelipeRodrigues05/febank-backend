package com.spring.bank.compliance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record KycSubmitDTO(
        @NotNull Long userId,
        @NotBlank String document
) {}
