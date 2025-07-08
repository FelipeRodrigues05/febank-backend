package com.spring.bank.common.validation.cpfcnpj;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CpfCnpjValidator implements ConstraintValidator<CpfCnpj, String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if(s == null || s.isBlank()) return false;

        String digits = s.replaceAll("\\D", "");
        return isValidCpf(digits) || isValidCnpj(digits);
    }

    private boolean isValidCpf(String digits) {
        return digits.length() == 11 && digits.chars().distinct().count() != 1;
    }

    private boolean isValidCnpj(String digits) {
        return digits.length() == 14 && digits.chars().distinct().count() != 1;
    }

}
