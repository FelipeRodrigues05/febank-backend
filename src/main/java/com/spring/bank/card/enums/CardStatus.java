package com.spring.bank.card.enums;

public enum CardStatus {
    ACTIVE, BLOCKED, CANCELED, EXPIRED;

    public boolean isUsable() {
        return this == ACTIVE;
    }
}
