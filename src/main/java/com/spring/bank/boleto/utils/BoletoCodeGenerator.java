package com.spring.bank.boleto.utils;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;

@Component
public class BoletoCodeGenerator {

    private static final String BANK_CODE = "341";
    private static final String CURRENCY_CODE = "9";
    private static final Random RANDOM = new Random();
    private static final long AMOUNT_MULTIPLIER = 100L;
    private static final long DUE_DATE_MODULUS = 10000L;
    private static final long RANDOM_FIELD_MODULUS = 99999999999999999L;

    public String generate(BigDecimal amount, LocalDate dueDate) {
        String amountStr = String.format("%010d", amount.multiply(BigDecimal.valueOf(AMOUNT_MULTIPLIER)).longValue());
        String dueDateFactor = String.format("%04d", dueDate.toEpochDay() % DUE_DATE_MODULUS);
        String randomField = String.format("%020d", Math.abs(RANDOM.nextLong() % RANDOM_FIELD_MODULUS));
        return BANK_CODE + CURRENCY_CODE + dueDateFactor + amountStr + randomField;
    }
}
