package com.spring.bank.compliance.service;

import com.spring.bank.compliance.enums.AmlFlagStatus;
import com.spring.bank.compliance.model.AmlFlag;
import com.spring.bank.compliance.repository.AmlFlagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AmlService {

    private static final BigDecimal COAF_THRESHOLD = new BigDecimal("10000.00");

    private final AmlFlagRepository amlFlagRepository;

    public void checkAndFlag(Long accountId, Long transactionId, BigDecimal amount) {
        if (amount.compareTo(COAF_THRESHOLD) >= 0) {
            AmlFlag flag = new AmlFlag();
            flag.setAccountId(accountId);
            flag.setTransactionId(transactionId);
            flag.setReason("Transaction above COAF threshold of R$10.000");
            flag.setAmount(amount);
            flag.setStatus(AmlFlagStatus.PENDING_REVIEW);
            amlFlagRepository.save(flag);
        }
    }

    public List<AmlFlag> listPendingFlags() {
        return amlFlagRepository.findByStatus(AmlFlagStatus.PENDING_REVIEW);
    }

    public void clearFlag(Long flagId) {
        AmlFlag flag = amlFlagRepository.findById(flagId)
                .orElseThrow(() -> new IllegalArgumentException("AML flag not found"));
        flag.setStatus(AmlFlagStatus.CLEARED);
        flag.setReviewedAt(LocalDateTime.now());
        amlFlagRepository.save(flag);
    }

    public void reportFlag(Long flagId) {
        AmlFlag flag = amlFlagRepository.findById(flagId)
                .orElseThrow(() -> new IllegalArgumentException("AML flag not found"));
        flag.setStatus(AmlFlagStatus.REPORTED);
        flag.setReviewedAt(LocalDateTime.now());
        amlFlagRepository.save(flag);
    }
}
