package com.spring.bank.pix.model;

import com.spring.bank.pix.enums.PixDevolutionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pix_devolutions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PixDevolution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long originalTransferId;

    private Long requesterAccountId;

    private BigDecimal amount;

    private String reason;

    @Enumerated(EnumType.STRING)
    private PixDevolutionStatus status = PixDevolutionStatus.PENDING;

    private LocalDateTime reviewedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
