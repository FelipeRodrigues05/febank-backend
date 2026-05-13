package com.spring.bank.twofa.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "trusted_devices")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrustedDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String deviceFingerprint;

    private String deviceName;

    private LocalDateTime expiresAt;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
