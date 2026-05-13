package com.spring.bank.audit.service;

import com.spring.bank.audit.enums.AuditAction;
import com.spring.bank.audit.model.AuditLog;
import com.spring.bank.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public void log(Long userId, AuditAction action, String description, String ipAddress) {
        AuditLog entry = new AuditLog();
        entry.setUserId(userId);
        entry.setAction(action);
        entry.setDescription(description);
        entry.setIpAddress(ipAddress);
        auditLogRepository.save(entry);
    }

    public void log(Long userId, AuditAction action, String description) {
        log(userId, action, description, "system");
    }

    public Page<AuditLog> listByUser(Long userId, Pageable pageable) {
        return auditLogRepository.findByUserId(userId, pageable);
    }
}
