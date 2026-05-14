package com.spring.bank.twofa.service;

import com.spring.bank.shared.email.EmailService;
import com.spring.bank.twofa.dto.OtpResponseDTO;
import com.spring.bank.twofa.model.OtpCode;
import com.spring.bank.twofa.repository.OtpRepository;
import com.spring.bank.user.model.User;
import com.spring.bank.user.service.FindUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpService {

    private static final int OTP_EXPIRY_MINUTES = 5;

    private final OtpRepository otpRepository;
    private final FindUserService findUserService;
    private final EmailService emailService;

    public OtpResponseDTO send(Long userId) {
        User user = findUserService.getById(userId);

        String code = String.format("%06d", new Random().nextInt(1000000));
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);

        OtpCode otpCode = new OtpCode();
        otpCode.setUserId(userId);
        otpCode.setCode(code);
        otpCode.setExpiresAt(expiresAt);
        otpCode.setUsed(false);
        otpRepository.save(otpCode);

        try {
            emailService.sendVerificationEmail(user.getEmail(), code);
        } catch (jakarta.mail.MessagingException messagingException) {
            throw new RuntimeException("Failed to send OTP email", messagingException);
        }

        return new OtpResponseDTO("OTP sent to registered email", expiresAt);
    }

    public boolean verify(Long userId, String code) {
        OtpCode otpCode = otpRepository.findByUserIdAndCodeAndUsedFalse(userId, code)
                .orElseThrow(() -> new IllegalArgumentException("Invalid OTP code"));

        if (otpCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("OTP has expired");
        }

        otpCode.setUsed(true);
        otpRepository.save(otpCode);

        return true;
    }
}
