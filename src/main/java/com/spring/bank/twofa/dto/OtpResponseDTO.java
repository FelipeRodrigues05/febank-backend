package com.spring.bank.twofa.dto;

import java.time.LocalDateTime;

public record OtpResponseDTO(
        String message,
        LocalDateTime expiresAt
) {}
