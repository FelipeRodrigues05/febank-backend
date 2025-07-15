package com.spring.bank.common.exception;

public class InvalidTransferAccountTypeException extends RuntimeException {
    public InvalidTransferAccountTypeException(String message) {
        super(message);
    }
}
