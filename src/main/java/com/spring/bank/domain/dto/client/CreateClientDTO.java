package com.spring.bank.domain.dto.client;

import jakarta.validation.constraints.NotBlank;

public record CreateClientDTO(
        @NotBlank String clientId,
        @NotBlank String name
) {}
