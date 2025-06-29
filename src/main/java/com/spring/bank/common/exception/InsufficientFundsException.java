package com.spring.bank.common.exception;

public class InsufficientFundsException extends RuntimeException  {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
