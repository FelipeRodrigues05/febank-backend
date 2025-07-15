package com.spring.bank.common.utils;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class AccountNumberGenerator {

    public String generateAccount() {
        String accountNumber = generateRandomNumber();
        String dv = generateMod11Digit(accountNumber);

        return accountNumber + dv;
    }

    private String generateRandomNumber() {
        int length = 8;

        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }

        return sb.toString();
    }

    private String generateMod11Digit(String account) {
        int sum = 0;
        int weight = 2; // INITIAL WEIGHT

        for (int i = account.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(account.charAt(i));
            sum += digit * weight;
            weight = (weight == 9) ? 2 : weight + 1;
        }

        int remainder = sum % 11; // EX.: 2
        int dv = 11 - remainder; // EX.: 11 - 2 = 9

        if (dv == 10) return "X";
        if (dv == 11) return "0";

        return String.valueOf(dv);
    }

}
