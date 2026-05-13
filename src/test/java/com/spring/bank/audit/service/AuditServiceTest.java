package com.spring.bank.audit.service;

import com.spring.bank.audit.enums.AuditAction;
import com.spring.bank.audit.model.AuditLog;
import com.spring.bank.audit.repository.AuditLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditService auditService;

    @Test
    void log_shouldSaveWithIpAddress() {
        when(auditLogRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        auditService.log(1L, AuditAction.USER_LOGIN, "User logged in", "192.168.0.1");

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());

        AuditLog saved = captor.getValue();
        assertThat(saved.getUserId()).isEqualTo(1L);
        assertThat(saved.getAction()).isEqualTo(AuditAction.USER_LOGIN);
        assertThat(saved.getIpAddress()).isEqualTo("192.168.0.1");
    }

    @Test
    void log_shouldSaveWithDefaultSystemIp() {
        when(auditLogRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        auditService.log(1L, AuditAction.USER_LOGIN, "User logged in");

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());

        AuditLog saved = captor.getValue();
        assertThat(saved.getUserId()).isEqualTo(1L);
        assertThat(saved.getIpAddress()).isEqualTo("system");
    }
}
