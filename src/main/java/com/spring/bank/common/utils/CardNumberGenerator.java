package com.spring.bank.common.utils;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CardNumberGenerator {
    private static final SecureRandom random = new SecureRandom();

    public static String generateCardNumber() {
        int length = 16;
        int[] digits = new int[length];

        digits[0] = 9;
        digits[1] = 9;
        digits[2] = 0;
        digits[3] = random.nextInt(10);
        digits[4] = random.nextInt(10);
        digits[5] = random.nextInt(10);

        for (int i = 6; i < length - 1; i++) {
            digits[i] = random.nextInt(10);
        }

        digits[length - 1] = calculateLuhnChecksum(digits);

        StringBuilder sb = new StringBuilder();
        for (int d : digits) {
            sb.append(d);
        }

        return sb.toString();
    }

    private static int calculateLuhnChecksum(int[] digits) {
        int sum = 0;
        boolean doubleDigit = true;

        for (int i = digits.length - 2; i >= 0; i--) {
            int d = digits[i];
            if (doubleDigit) {
                d = d * 2;
                if (d > 9) d -= 9;
            }
            sum += d;
            doubleDigit = !doubleDigit;
        }

        return (10 - (sum % 10)) % 10;
    }

    public static String generateCVV() {
        int cvv = random.nextInt(900) + 100; // Garante entre 100-999
        return String.valueOf(cvv);
    }

    public static LocalDate generateExpirationDate() {
        LocalDate now = LocalDate.now();
        int yearsToAdd = 2 + random.nextInt(4); // 2 a 5 anos

        return now.plusYears(yearsToAdd);
    }
}
