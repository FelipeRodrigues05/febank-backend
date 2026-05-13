package com.spring.bank.scheduling.model;

import com.spring.bank.scheduling.enums.ScheduledPaymentStatus;
import com.spring.bank.scheduling.enums.ScheduledPaymentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "scheduled_payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long fromAccountId;

    @Enumerated(EnumType.STRING)
    private ScheduledPaymentType type;

    private BigDecimal amount;

    private String targetIdentifier;

    private String description;

    private LocalDate scheduledDate;

    @Enumerated(EnumType.STRING)
    private ScheduledPaymentStatus status = ScheduledPaymentStatus.PENDING;

    private LocalDateTime executedAt;

    private String failureReason;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
