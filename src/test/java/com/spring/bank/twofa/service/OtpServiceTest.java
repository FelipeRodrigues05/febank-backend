package com.spring.bank.twofa.service;

import com.spring.bank.shared.email.EmailService;
import com.spring.bank.twofa.model.OtpCode;
import com.spring.bank.twofa.repository.OtpRepository;
import com.spring.bank.user.service.FindUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OtpServiceTest {

    @Mock
    private OtpRepository otpRepository;

    @Mock
    private FindUserService findUserService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private OtpService otpService;

    @Test
    void verify_shouldReturnTrueForValidCode() {
        OtpCode otpCode = new OtpCode();
        otpCode.setUserId(1L);
        otpCode.setCode("123456");
        otpCode.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        otpCode.setUsed(false);

        when(otpRepository.findByUserIdAndCodeAndUsedFalse(1L, "123456"))
                .thenReturn(Optional.of(otpCode));
        when(otpRepository.save(any())).thenReturn(otpCode);

        boolean result = otpService.verify(1L, "123456");

        assertThat(result).isTrue();
        verify(otpRepository).save(any());
    }

    @Test
    void verify_shouldThrowWhenExpired() {
        OtpCode otpCode = new OtpCode();
        otpCode.setUserId(1L);
        otpCode.setCode("123456");
        otpCode.setExpiresAt(LocalDateTime.now().minusMinutes(1));
        otpCode.setUsed(false);

        when(otpRepository.findByUserIdAndCodeAndUsedFalse(1L, "123456"))
                .thenReturn(Optional.of(otpCode));

        assertThatThrownBy(() -> otpService.verify(1L, "123456"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("expired");
    }

    @Test
    void verify_shouldThrowWhenNotFound() {
        when(otpRepository.findByUserIdAndCodeAndUsedFalse(1L, "999999"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> otpService.verify(1L, "999999"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid OTP");
    }
}
