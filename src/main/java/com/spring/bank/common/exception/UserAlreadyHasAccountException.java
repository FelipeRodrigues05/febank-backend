package com.spring.bank.common.exception;

public class UserAlreadyHasAccountException extends Exception {
    public UserAlreadyHasAccountException(String message) {
        super(message);
    }
}
