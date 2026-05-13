package com.spring.bank.twofa.repository;

import com.spring.bank.twofa.model.TrustedDevice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TrustedDeviceRepository extends JpaRepository<TrustedDevice, Long> {
    boolean existsByUserIdAndDeviceFingerprintAndExpiresAtAfter(Long userId, String fingerprint, LocalDateTime now);
    List<TrustedDevice> findByUserId(Long userId);
    void deleteByUserIdAndDeviceFingerprint(Long userId, String fingerprint);
}
