package com.spring.bank.pix.model;

import com.spring.bank.account.model.Account;
import com.spring.bank.pix.enums.ScheduledPixStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "scheduled_pix")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledPix {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "from_account_id")
    private Account fromAccount;

    private String pixKey;

    private BigDecimal amount;

    private String description;

    private LocalDate scheduledDate;

    @Enumerated(EnumType.STRING)
    private ScheduledPixStatus status = ScheduledPixStatus.PENDING;

    private LocalDateTime executedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
