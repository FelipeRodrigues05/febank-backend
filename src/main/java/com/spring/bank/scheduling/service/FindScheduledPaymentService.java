package com.spring.bank.scheduling.service;

import com.spring.bank.scheduling.dto.ScheduledPaymentResponseDTO;
import com.spring.bank.scheduling.enums.ScheduledPaymentStatus;
import com.spring.bank.scheduling.model.ScheduledPayment;
import com.spring.bank.scheduling.repository.ScheduledPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FindScheduledPaymentService {

    private final ScheduledPaymentRepository scheduledPaymentRepository;

    public List<ScheduledPaymentResponseDTO> listByAccount(Long accountId) {
        return scheduledPaymentRepository.findByFromAccountId(accountId).stream()
                .map(ScheduledPaymentResponseDTO::new)
                .toList();
    }

    public void cancel(Long id, Long accountId) {
        ScheduledPayment payment = scheduledPaymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Scheduled payment not found: " + id));

        if (!payment.getFromAccountId().equals(accountId)) {
            throw new IllegalArgumentException("This payment does not belong to account: " + accountId);
        }

        if (payment.getStatus() != ScheduledPaymentStatus.PENDING) {
            throw new IllegalArgumentException("Cannot cancel a non-pending payment");
        }

        payment.setStatus(ScheduledPaymentStatus.CANCELLED);
        scheduledPaymentRepository.save(payment);
    }
}
