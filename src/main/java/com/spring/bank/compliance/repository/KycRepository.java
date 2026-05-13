package com.spring.bank.compliance.repository;

import com.spring.bank.compliance.model.KycRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KycRepository extends JpaRepository<KycRecord, Long> {
    Optional<KycRecord> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}
