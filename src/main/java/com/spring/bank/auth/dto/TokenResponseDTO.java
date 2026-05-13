package com.spring.bank.auth.dto;

public record TokenResponseDTO(
        String accessToken,
        String tokenType,
        long expiresIn
) {}
