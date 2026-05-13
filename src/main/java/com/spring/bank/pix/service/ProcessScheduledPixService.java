package com.spring.bank.pix.service;

import com.spring.bank.account.model.Account;
import com.spring.bank.account.service.AccountFundsService;
import com.spring.bank.pix.enums.ScheduledPixStatus;
import com.spring.bank.pix.model.PixKey;
import com.spring.bank.pix.model.ScheduledPix;
import com.spring.bank.pix.repository.ScheduledPixRepository;
import com.spring.bank.transaction.dto.CreateTransactionDTO;
import com.spring.bank.transaction.enums.TransactionTypeEnum;
import com.spring.bank.transaction.service.CreateTransactionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcessScheduledPixService {

    private static final Logger log = LoggerFactory.getLogger(ProcessScheduledPixService.class);

    private final ScheduledPixRepository scheduledPixRepository;
    private final FindPixKeyService findPixKeyService;
    private final AccountFundsService accountFundsService;
    private final CreateTransactionService createTransactionService;

    public void processToday() {
        List<ScheduledPix> pending = scheduledPixRepository
                .findByStatusAndScheduledDateLessThanEqual(ScheduledPixStatus.PENDING, LocalDate.now());

        for (ScheduledPix scheduledPix : pending) {
            try {
                execute(scheduledPix);
            } catch (Exception exception) {
                log.warn("Failed to process scheduled PIX id={}: {}", scheduledPix.getId(), exception.getMessage());
                scheduledPix.setStatus(ScheduledPixStatus.FAILED);
                scheduledPixRepository.save(scheduledPix);
            }
        }
    }

    private void execute(ScheduledPix scheduledPix) {
        Account fromAccount = scheduledPix.getFromAccount();
        PixKey pixKey = findPixKeyService.getByKey(scheduledPix.getPixKey());
        Account toAccount = pixKey.getAccount();

        String description = scheduledPix.getDescription() != null
                ? scheduledPix.getDescription()
                : "Scheduled PIX: " + scheduledPix.getPixKey();

        accountFundsService.subtractFunds(fromAccount.getId(), scheduledPix.getAmount());
        accountFundsService.addFunds(toAccount.getId(), scheduledPix.getAmount());

        createTransactionService.create(new CreateTransactionDTO(fromAccount, TransactionTypeEnum.DEBIT, scheduledPix.getAmount(), description));
        createTransactionService.create(new CreateTransactionDTO(toAccount, TransactionTypeEnum.CREDIT, scheduledPix.getAmount(), description));

        scheduledPix.setStatus(ScheduledPixStatus.EXECUTED);
        scheduledPix.setExecutedAt(LocalDateTime.now());
        scheduledPixRepository.save(scheduledPix);
    }
}
