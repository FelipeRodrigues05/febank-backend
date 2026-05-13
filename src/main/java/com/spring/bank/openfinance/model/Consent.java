package com.spring.bank.openfinance.model;

import com.spring.bank.openfinance.enums.ConsentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "open_finance_consents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Consent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private Long userId;

    private String clientId;

    @Column(length = 500)
    private String permissions;

    @Enumerated(EnumType.STRING)
    private ConsentStatus status = ConsentStatus.AWAITING_AUTHORISATION;

    private LocalDateTime expiresAt;

    private LocalDateTime authorisedAt;

    private LocalDateTime revokedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
