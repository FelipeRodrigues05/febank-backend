package com.spring.bank.common.exception;

public class SavingsBoxNotFoundException extends RuntimeException {
    public SavingsBoxNotFoundException(String message) {
        super(message);
    }
}
