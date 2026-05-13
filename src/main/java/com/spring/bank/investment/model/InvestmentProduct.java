package com.spring.bank.investment.model;

import com.spring.bank.investment.enums.ProductType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "investment_products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private ProductType type;

    private BigDecimal annualRate;

    private BigDecimal cdiPercentage;

    private BigDecimal minimumAmount;

    private int minimumDaysToRedeem;

    private boolean irExempt;

    private boolean active = true;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
