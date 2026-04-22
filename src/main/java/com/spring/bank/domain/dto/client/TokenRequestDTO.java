package com.spring.bank.domain.dto.client;

import jakarta.validation.constraints.NotBlank;

public record TokenRequestDTO(
        @NotBlank String client_id,
        @NotBlank String client_secret,
        @NotBlank String grant_type
) {}
