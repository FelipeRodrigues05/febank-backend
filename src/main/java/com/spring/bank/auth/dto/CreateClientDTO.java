package com.spring.bank.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateClientDTO(
        @NotBlank String clientId,
        @NotBlank String name
) {}
