package com.spring.bank.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "pix_contacts")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PixContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(nullable = false)
    private String alias;

    @Column(name = "pix_key", nullable = false)
    private String pixKey;

    @Column(nullable = false)
    private int transferCount = 0;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
