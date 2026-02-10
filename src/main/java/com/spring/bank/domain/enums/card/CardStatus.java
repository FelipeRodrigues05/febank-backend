package com.spring.bank.domain.enums.card;

public enum CardStatus {
    ACTIVE, BLOCKED, CANCELED, EXPIRED;

    public boolean isUsable() {
        return this == ACTIVE;
    }
}
