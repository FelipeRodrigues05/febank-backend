package com.spring.bank.pix.model;

import com.spring.bank.account.model.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pix_limits", uniqueConstraints = @UniqueConstraint(columnNames = "account_id"))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PixLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    private BigDecimal dailyLimit = new BigDecimal("5000.00");

    private BigDecimal singleTransactionLimit = new BigDecimal("1000.00");

    private BigDecimal nightLimit = new BigDecimal("1000.00");

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
