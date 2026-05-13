package com.spring.bank.loan.model;

import com.spring.bank.account.model.Account;
import com.spring.bank.loan.enums.LoanStatus;
import com.spring.bank.loan.enums.LoanType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "loans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Enumerated(EnumType.STRING)
    private LoanType type;

    private BigDecimal requestedAmount;

    private BigDecimal approvedAmount;

    private BigDecimal interestRate;

    private int totalInstallments;

    private int paidInstallments = 0;

    private BigDecimal installmentAmount;

    @Enumerated(EnumType.STRING)
    private LoanStatus status;

    private int creditScoreAtApplication;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
