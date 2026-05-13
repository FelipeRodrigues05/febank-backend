package com.spring.bank.twofa.repository;

import com.spring.bank.twofa.model.OtpCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<OtpCode, Long> {
    Optional<OtpCode> findByUserIdAndCodeAndUsedFalse(Long userId, String code);
    void deleteByUserIdAndUsedTrue(Long userId);
}
