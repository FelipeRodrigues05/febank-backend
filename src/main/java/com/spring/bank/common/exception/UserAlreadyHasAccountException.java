package com.spring.bank.common.exception;

public class UserAlreadyHasAccountException extends RuntimeException  {
    public UserAlreadyHasAccountException(String message) {
        super(message);
    }
}
