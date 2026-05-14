package com.spring.bank.common.exception;

public class CardBillNotFoundException extends RuntimeException {
    public CardBillNotFoundException(String message) {
        super(message);
    }
}
