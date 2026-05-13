package com.spring.bank.pix.model;

import com.spring.bank.account.model.Account;
import com.spring.bank.pix.enums.PixQrCodeType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pix_qr_codes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PixQrCode {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Enumerated(EnumType.STRING)
    private PixQrCodeType type;

    private String pixKey;

    private BigDecimal amount;

    private String description;

    @Column(length = 2048)
    private String payload;

    private boolean active = true;

    private LocalDateTime expiresAt;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
