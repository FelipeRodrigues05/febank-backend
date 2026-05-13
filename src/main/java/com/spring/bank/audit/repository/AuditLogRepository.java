package com.spring.bank.audit.repository;

import com.spring.bank.audit.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByUserIdOrderByCreatedAtDesc(Long userId);
    Page<AuditLog> findByUserId(Long userId, Pageable pageable);
}
