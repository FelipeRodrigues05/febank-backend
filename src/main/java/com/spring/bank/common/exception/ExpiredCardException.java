package com.spring.bank.common.exception;

public class ExpiredCardException extends RuntimeException {
    public ExpiredCardException(String message) {
        super(message);
    }
}
