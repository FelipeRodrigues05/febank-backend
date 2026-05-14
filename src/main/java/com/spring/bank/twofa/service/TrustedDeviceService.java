package com.spring.bank.twofa.service;

import com.spring.bank.twofa.dto.TrustDeviceDTO;
import com.spring.bank.twofa.dto.TrustedDeviceResponseDTO;
import com.spring.bank.twofa.model.TrustedDevice;
import com.spring.bank.twofa.repository.TrustedDeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrustedDeviceService {

    private static final int DEVICE_TRUST_DAYS = 30;

    private final TrustedDeviceRepository trustedDeviceRepository;

    public TrustedDeviceResponseDTO trust(TrustDeviceDTO dto) {
        TrustedDevice device = new TrustedDevice();
        device.setUserId(dto.userId());
        device.setDeviceFingerprint(dto.deviceFingerprint());
        device.setDeviceName(dto.deviceName());
        device.setExpiresAt(LocalDateTime.now().plusDays(DEVICE_TRUST_DAYS));
        TrustedDevice saved = trustedDeviceRepository.save(device);
        return new TrustedDeviceResponseDTO(saved);
    }

    public boolean isTrusted(Long userId, String fingerprint) {
        return trustedDeviceRepository.existsByUserIdAndDeviceFingerprintAndExpiresAtAfter(
                userId, fingerprint, LocalDateTime.now()
        );
    }

    public List<TrustedDeviceResponseDTO> listDevices(Long userId) {
        return trustedDeviceRepository.findByUserId(userId).stream()
                .map(TrustedDeviceResponseDTO::new)
                .toList();
    }

    public void revoke(Long userId, String fingerprint) {
        trustedDeviceRepository.deleteByUserIdAndDeviceFingerprint(userId, fingerprint);
    }
}
