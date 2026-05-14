package com.spring.bank.twofa.service;

import com.spring.bank.twofa.dto.TrustDeviceDTO;
import com.spring.bank.twofa.dto.TrustedDeviceResponseDTO;
import com.spring.bank.twofa.model.TrustedDevice;
import com.spring.bank.twofa.repository.TrustedDeviceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrustedDeviceServiceTest {

    @Mock
    private TrustedDeviceRepository trustedDeviceRepository;

    @InjectMocks
    private TrustedDeviceService trustedDeviceService;

    @Test
    void trust_shouldSaveAndReturnResponse() {
        TrustedDevice savedDevice = new TrustedDevice();
        savedDevice.setId(1L);
        savedDevice.setUserId(1L);
        savedDevice.setDeviceFingerprint("fp-abc");
        savedDevice.setDeviceName("My Phone");
        savedDevice.setExpiresAt(LocalDateTime.now().plusDays(30));

        when(trustedDeviceRepository.save(any())).thenReturn(savedDevice);

        TrustDeviceDTO dto = new TrustDeviceDTO(1L, "fp-abc", "My Phone");
        TrustedDeviceResponseDTO result = trustedDeviceService.trust(dto);

        assertThat(result).isNotNull();
        assertThat(result.deviceFingerprint()).isEqualTo("fp-abc");
    }

    @Test
    void isTrusted_shouldReturnTrueWhenValid() {
        when(trustedDeviceRepository.existsByUserIdAndDeviceFingerprintAndExpiresAtAfter(
                eq(1L), eq("fp-abc"), any(LocalDateTime.class)))
                .thenReturn(true);

        boolean result = trustedDeviceService.isTrusted(1L, "fp-abc");

        assertThat(result).isTrue();
    }

    @Test
    void isTrusted_shouldReturnFalseWhenExpired() {
        when(trustedDeviceRepository.existsByUserIdAndDeviceFingerprintAndExpiresAtAfter(
                eq(1L), eq("fp-abc"), any(LocalDateTime.class)))
                .thenReturn(false);

        boolean result = trustedDeviceService.isTrusted(1L, "fp-abc");

        assertThat(result).isFalse();
    }

    @Test
    void revoke_shouldCallDeleteByUserAndFingerprint() {
        trustedDeviceService.revoke(1L, "fp");

        verify(trustedDeviceRepository).deleteByUserIdAndDeviceFingerprint(1L, "fp");
    }
}
