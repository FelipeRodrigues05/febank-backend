package com.spring.bank.domain.model;

import com.spring.bank.domain.enums.card.CardStatus;
import com.spring.bank.domain.enums.card.CardType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "cards")
@Getter
@Data
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private CardType cardType;

    @Enumerated(EnumType.STRING)
    private CardStatus cardStatus;

    private String number;
    private String cvv;
    private LocalDateTime expirationDate;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
