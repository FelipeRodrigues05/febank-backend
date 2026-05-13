package com.spring.bank.compliance.service;

import com.spring.bank.compliance.enums.KycStatus;
import com.spring.bank.compliance.model.KycRecord;
import com.spring.bank.compliance.repository.KycRepository;
import com.spring.bank.user.service.FindUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class KycService {

    private final KycRepository kycRepository;
    private final FindUserService findUserService;

    public KycRecord submit(Long userId, String document) {
        findUserService.getById(userId);

        if (kycRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("KYC already submitted for this user");
        }

        validateDocument(document);

        KycRecord record = new KycRecord();
        record.setUserId(userId);
        record.setStatus(KycStatus.APPROVED);
        record.setReviewedAt(LocalDateTime.now());

        return kycRepository.save(record);
    }

    public KycRecord getStatus(Long userId) {
        return kycRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("KYC not found for user"));
    }

    private void validateDocument(String document) {
        String digits = document.replaceAll("\\D", "");

        if (digits.length() == 11) {
            validateCpf(digits);
        } else if (digits.length() == 14) {
            validateCnpj(digits);
        } else {
            throw new IllegalArgumentException("Invalid CPF/CNPJ format");
        }
    }

    private void validateCpf(String digits) {
        if (allDigitsSame(digits)) {
            throw new IllegalArgumentException("Invalid CPF/CNPJ format");
        }

        int[] weights1 = {10, 9, 8, 7, 6, 5, 4, 3, 2};
        int sum1 = 0;
        for (int index = 0; index < 9; index++) {
            sum1 += Character.getNumericValue(digits.charAt(index)) * weights1[index];
        }
        int remainder1 = (sum1 * 10) % 11;
        int firstCheckDigit = remainder1 == 10 ? 0 : remainder1;

        int[] weights2 = {11, 10, 9, 8, 7, 6, 5, 4, 3, 2};
        int sum2 = 0;
        for (int index = 0; index < 10; index++) {
            sum2 += Character.getNumericValue(digits.charAt(index)) * weights2[index];
        }
        int remainder2 = (sum2 * 10) % 11;
        int secondCheckDigit = remainder2 == 10 ? 0 : remainder2;

        boolean valid = firstCheckDigit == Character.getNumericValue(digits.charAt(9))
                && secondCheckDigit == Character.getNumericValue(digits.charAt(10));

        if (!valid) {
            throw new IllegalArgumentException("Invalid CPF/CNPJ format");
        }
    }

    private void validateCnpj(String digits) {
        if (allDigitsSame(digits)) {
            throw new IllegalArgumentException("Invalid CPF/CNPJ format");
        }

        int[] weights1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int sum1 = 0;
        for (int index = 0; index < 12; index++) {
            sum1 += Character.getNumericValue(digits.charAt(index)) * weights1[index];
        }
        int remainder1 = sum1 % 11;
        int firstCheckDigit = remainder1 < 2 ? 0 : 11 - remainder1;

        int[] weights2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int sum2 = 0;
        for (int index = 0; index < 13; index++) {
            sum2 += Character.getNumericValue(digits.charAt(index)) * weights2[index];
        }
        int remainder2 = sum2 % 11;
        int secondCheckDigit = remainder2 < 2 ? 0 : 11 - remainder2;

        boolean valid = firstCheckDigit == Character.getNumericValue(digits.charAt(12))
                && secondCheckDigit == Character.getNumericValue(digits.charAt(13));

        if (!valid) {
            throw new IllegalArgumentException("Invalid CPF/CNPJ format");
        }
    }

    private boolean allDigitsSame(String digits) {
        char first = digits.charAt(0);
        for (int index = 1; index < digits.length(); index++) {
            if (digits.charAt(index) != first) {
                return false;
            }
        }
        return true;
    }
}
