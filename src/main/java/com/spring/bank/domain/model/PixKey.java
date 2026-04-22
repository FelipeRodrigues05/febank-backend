package com.spring.bank.domain.model;

import com.spring.bank.domain.enums.pix.PixKeyType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "pix_keys")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PixKey {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PixKeyType type;

    @Column(name = "pix_key", nullable = false, unique = true)
    private String key;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
