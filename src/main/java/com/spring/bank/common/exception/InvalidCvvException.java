package com.spring.bank.common.exception;

public class InvalidCvvException extends RuntimeException {
    public InvalidCvvException(String message) {
        super(message);
    }
}
