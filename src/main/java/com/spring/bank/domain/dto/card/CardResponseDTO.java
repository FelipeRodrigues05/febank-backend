package com.spring.bank.domain.dto.card;

import com.spring.bank.domain.enums.card.CardStatus;
import com.spring.bank.domain.enums.card.CardType;
import com.spring.bank.domain.model.Card;
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
    private String number;
    private CardType type;
    private CardStatus status;
    private LocalDate expirationDate;
    private String cvv;

    private String ownerName;
    private String ownerDocument;

    private LocalDateTime createdAt;

    public CardResponseDTO(Card card) {
        this.id = card.getId();
        this.number = card.getNumber();
        this.type = card.getCardType();
        this.status = card.getCardStatus();
        this.expirationDate = card.getExpirationDate();
        this.cvv = card.getCvv();

        this.ownerName = card.getAccount().getUser().getName();
        this.ownerDocument = card.getAccount().getUser().getDocument();

        this.createdAt = card.getCreatedAt();

    }
}
