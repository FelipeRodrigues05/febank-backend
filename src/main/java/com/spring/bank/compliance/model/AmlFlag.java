package com.spring.bank.compliance.model;

import com.spring.bank.compliance.enums.AmlFlagStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "aml_flags")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AmlFlag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long accountId;

    private Long transactionId;

    private String reason;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private AmlFlagStatus status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime reviewedAt;
}
