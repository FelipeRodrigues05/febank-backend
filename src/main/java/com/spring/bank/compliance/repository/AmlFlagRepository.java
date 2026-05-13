package com.spring.bank.compliance.repository;

import com.spring.bank.compliance.enums.AmlFlagStatus;
import com.spring.bank.compliance.model.AmlFlag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AmlFlagRepository extends JpaRepository<AmlFlag, Long> {
    List<AmlFlag> findByAccountIdAndStatus(Long accountId, AmlFlagStatus status);
    List<AmlFlag> findByStatus(AmlFlagStatus status);
}
