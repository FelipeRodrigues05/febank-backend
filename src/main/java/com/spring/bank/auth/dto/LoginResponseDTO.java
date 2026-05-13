package com.spring.bank.auth.dto;

import com.spring.bank.user.dto.UserResponseDTO;

public record LoginResponseDTO(
        String accessToken,
        String tokenType,
        long expiresIn,
        UserResponseDTO user
) {}
