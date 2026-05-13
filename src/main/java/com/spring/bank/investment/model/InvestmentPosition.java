package com.spring.bank.investment.model;

import com.spring.bank.account.model.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "investment_positions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private InvestmentProduct product;

    private BigDecimal investedAmount;

    private BigDecimal currentAmount;

    @CreationTimestamp
    private LocalDateTime appliedAt;

    private LocalDateTime redeemedAt;

    private boolean active = true;
}
