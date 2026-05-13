package com.spring.bank.twofa.dto;

import com.spring.bank.twofa.model.TrustedDevice;

import java.time.LocalDateTime;

public record TrustedDeviceResponseDTO(
        Long id,
        String deviceName,
        String deviceFingerprint,
        LocalDateTime expiresAt,
        LocalDateTime createdAt
) {
    public TrustedDeviceResponseDTO(TrustedDevice device) {
        this(device.getId(), device.getDeviceName(), device.getDeviceFingerprint(), device.getExpiresAt(), device.getCreatedAt());
    }
}
