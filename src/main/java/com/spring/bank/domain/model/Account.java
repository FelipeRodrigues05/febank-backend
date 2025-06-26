package com.spring.bank.domain.model;

import com.spring.bank.domain.enums.account.AccountStatusEnum;
import com.spring.bank.domain.enums.account.AccountTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "accounts")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany
    private List<Transaction> transactions;

    private String number;

    @Enumerated(EnumType.STRING)
    private AccountTypeEnum type;

    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    private AccountStatusEnum status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
