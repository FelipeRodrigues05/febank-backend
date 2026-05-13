package com.spring.bank.card.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cashback_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CashbackRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal transactionAmount;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cashbackAmount;

    @Column(nullable = false)
    private boolean credited = false;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
