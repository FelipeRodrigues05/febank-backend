package com.spring.bank.scheduling.service;

import com.spring.bank.account.model.Account;
import com.spring.bank.account.service.FindAccountService;
import com.spring.bank.pix.dto.PixTransferDTO;
import com.spring.bank.pix.service.PixTransferService;
import com.spring.bank.scheduling.enums.ScheduledPaymentStatus;
import com.spring.bank.scheduling.model.ScheduledPayment;
import com.spring.bank.scheduling.repository.ScheduledPaymentRepository;
import com.spring.bank.transfer.dto.CreateTransferDTO;
import com.spring.bank.transfer.service.CreateTransferService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcessScheduledPaymentsService {

    private static final Logger log = LoggerFactory.getLogger(ProcessScheduledPaymentsService.class);

    private final ScheduledPaymentRepository scheduledPaymentRepository;
    private final FindAccountService findAccountService;
    private final CreateTransferService createTransferService;
    private final PixTransferService pixTransferService;

    public void processToday() {
        List<ScheduledPayment> pendingPayments = scheduledPaymentRepository
                .findByStatusAndScheduledDateLessThanEqual(ScheduledPaymentStatus.PENDING, LocalDate.now());

        for (ScheduledPayment payment : pendingPayments) {
            try {
                switch (payment.getType()) {
                    case TRANSFER -> executeTransfer(payment);
                    case PIX -> executePixTransfer(payment);
                    case BOLETO -> failAsBoletoNotAutomated(payment);
                }
                payment.setStatus(ScheduledPaymentStatus.EXECUTED);
                payment.setExecutedAt(LocalDateTime.now());
            } catch (Exception error) {
                payment.setStatus(ScheduledPaymentStatus.FAILED);
                payment.setFailureReason(error.getMessage());
                log.warn("Scheduled payment id={} failed: {}", payment.getId(), error.getMessage());
            }
        }

        scheduledPaymentRepository.saveAll(pendingPayments);
    }

    private void executeTransfer(ScheduledPayment payment) {
        Account toAccount = findAccountService.getByNumber(payment.getTargetIdentifier());
        createTransferService.create(new CreateTransferDTO(
                payment.getFromAccountId(),
                toAccount.getId(),
                payment.getAmount(),
                payment.getDescription()
        ));
    }

    private void executePixTransfer(ScheduledPayment payment) {
        pixTransferService.transfer(new PixTransferDTO(
                payment.getFromAccountId(),
                payment.getTargetIdentifier(),
                payment.getAmount(),
                payment.getDescription()
        ));
    }

    private void failAsBoletoNotAutomated(ScheduledPayment payment) {
        log.info("Boleto scheduled payment id={} requires manual processing", payment.getId());
        throw new UnsupportedOperationException("Boleto payment requires manual processing");
    }
}
