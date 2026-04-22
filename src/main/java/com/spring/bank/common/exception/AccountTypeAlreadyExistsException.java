package com.spring.bank.common.exception;

public class AccountTypeAlreadyExistsException extends RuntimeException {
    public AccountTypeAlreadyExistsException(String message) {
        super(message);
    }
}
