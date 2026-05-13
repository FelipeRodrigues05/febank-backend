package com.spring.bank.card.dto;

import com.spring.bank.card.enums.CardStatus;
import com.spring.bank.card.enums.CardType;
import com.spring.bank.card.model.Card;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardResponseDTO {

    private String id;
    private String maskedNumber;
    private CardType type;
    private CardStatus status;
    private LocalDate expirationDate;
    private String ownerName;
    private LocalDateTime createdAt;

    public CardResponseDTO(Card card) {
        this.id             = card.getId();
        this.maskedNumber   = maskCardNumber(card.getNumber());
        this.type           = card.getCardType();
        this.status         = card.getCardStatus();
        this.expirationDate = card.getExpirationDate();
        this.ownerName      = card.getAccount().getUser().getName();
        this.createdAt      = card.getCreatedAt();
    }

    private String maskCardNumber(String number) {
        if (number == null || number.length() < 4) return "****";
        return "**** **** **** " + number.substring(number.length() - 4);
    }
}
