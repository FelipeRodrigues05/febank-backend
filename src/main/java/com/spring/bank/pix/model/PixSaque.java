package com.spring.bank.pix.model;

import com.spring.bank.account.model.Account;
import com.spring.bank.pix.enums.PixSaqueType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pix_saques")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PixSaque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Enumerated(EnumType.STRING)
    private PixSaqueType type;

    private BigDecimal totalAmount;

    private BigDecimal withdrawalAmount;

    private Long merchantAccountId;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
