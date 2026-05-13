package com.spring.bank.card.model;

import com.spring.bank.card.enums.LimitIncreaseStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "limit_increase_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LimitIncreaseRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal requestedLimit;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal currentLimit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LimitIncreaseStatus status;

    private LocalDateTime reviewedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
