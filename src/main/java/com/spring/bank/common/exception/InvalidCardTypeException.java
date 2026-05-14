package com.spring.bank.common.exception;

public class InvalidCardTypeException extends RuntimeException {
    public InvalidCardTypeException(String message) {
        super(message);
    }
}
