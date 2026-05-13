package com.spring.bank.twofa.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "otp_codes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String code;

    private LocalDateTime expiresAt;

    private boolean used;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
