package com.spring.bank.common.exception;

public class PixKeyLimitExceededException extends RuntimeException {
    public PixKeyLimitExceededException(String message) {
        super(message);
    }
}
