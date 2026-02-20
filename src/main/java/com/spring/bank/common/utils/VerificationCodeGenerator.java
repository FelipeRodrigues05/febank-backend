package com.spring.bank.common.utils;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class VerificationCodeGenerator {
    public String generate() {
        int length = 4;

        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }

        return sb.toString();
    }
}
