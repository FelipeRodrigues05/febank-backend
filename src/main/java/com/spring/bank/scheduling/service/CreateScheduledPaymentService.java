package com.spring.bank.scheduling.service;

import com.spring.bank.account.service.FindAccountService;
import com.spring.bank.scheduling.dto.CreateScheduledPaymentDTO;
import com.spring.bank.scheduling.enums.ScheduledPaymentStatus;
import com.spring.bank.scheduling.model.ScheduledPayment;
import com.spring.bank.scheduling.repository.ScheduledPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CreateScheduledPaymentService {

    private final ScheduledPaymentRepository scheduledPaymentRepository;
    private final FindAccountService findAccountService;

    public ScheduledPayment schedule(CreateScheduledPaymentDTO dto) {
        findAccountService.getById(dto.fromAccountId());

        if (dto.scheduledDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Scheduled date must be today or in the future");
        }

        ScheduledPayment payment = new ScheduledPayment();
        payment.setFromAccountId(dto.fromAccountId());
        payment.setType(dto.type());
        payment.setAmount(dto.amount());
        payment.setTargetIdentifier(dto.targetIdentifier());
        payment.setDescription(dto.description());
        payment.setScheduledDate(dto.scheduledDate());
        payment.setStatus(ScheduledPaymentStatus.PENDING);

        return scheduledPaymentRepository.save(payment);
    }
}
