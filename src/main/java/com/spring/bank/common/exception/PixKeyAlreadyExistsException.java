package com.spring.bank.common.exception;

public class PixKeyAlreadyExistsException extends RuntimeException {
    public PixKeyAlreadyExistsException(String message) {
        super(message);
    }
}
