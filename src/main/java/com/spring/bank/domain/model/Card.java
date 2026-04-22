package com.spring.bank.domain.model;

import com.spring.bank.domain.enums.card.CardStatus;
import com.spring.bank.domain.enums.card.CardType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cards")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Enumerated(EnumType.STRING)
    private CardType cardType;

    @Enumerated(EnumType.STRING)
    private CardStatus cardStatus;

    private BigDecimal limitAvailable;
    private BigDecimal usedLimit;

    private String number;
    private String cvv;
    private LocalDate expirationDate;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Override
    public String toString() {
        return "Card{id='" + id + "', cardType=" + cardType + ", cardStatus=" + cardStatus + ", expirationDate=" + expirationDate + "}";
    }
}
