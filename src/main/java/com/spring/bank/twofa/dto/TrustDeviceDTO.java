package com.spring.bank.twofa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TrustDeviceDTO(
        @NotNull Long userId,
        @NotBlank String deviceFingerprint,
        @NotBlank String deviceName
) {}
