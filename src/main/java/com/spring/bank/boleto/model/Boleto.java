package com.spring.bank.boleto.model;

import com.spring.bank.boleto.enums.BoletoStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "boletos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Boleto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long issuerAccountId;

    private String payerDocument;

    private BigDecimal amount;

    private String description;

    @Column(unique = true)
    private String boletoCode;

    @Enumerated(EnumType.STRING)
    private BoletoStatus status = BoletoStatus.PENDING;

    private LocalDate dueDate;

    private LocalDateTime paidAt;

    private Long paidByAccountId;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
