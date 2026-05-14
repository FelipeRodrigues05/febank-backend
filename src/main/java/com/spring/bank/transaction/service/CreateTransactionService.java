package com.spring.bank.transaction.service;

import com.spring.bank.transaction.dto.CreateTransactionDTO;
import com.spring.bank.transaction.enums.TransactionStatusEnum;
import com.spring.bank.transaction.messaging.TransactionPublisher;
import com.spring.bank.transaction.model.Transaction;
import com.spring.bank.transaction.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreateTransactionService {

    private static final Logger log = LoggerFactory.getLogger(CreateTransactionService.class);

    private final TransactionRepository transactionRepository;
    private final TransactionPublisher transactionPublisher;
    private final TransactionCategorizationService transactionCategorizationService;

    @Transactional
    public void create(CreateTransactionDTO data) {
        Transaction transaction = new Transaction();
        transaction.setAccount(data.account());
        transaction.setType(data.type());
        transaction.setAmount(data.amount());
        transaction.setDescription(data.description() != null ? data.description() : "");
        transaction.setCategory(transactionCategorizationService.categorize(data.description()));
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setStatus(TransactionStatusEnum.PENDING);

        transactionRepository.save(transaction);
        transactionPublisher.publish(transaction.getId());

        log.debug("Transaction queued: id={} type={} amount={}", transaction.getId(), data.type(), data.amount());
    }
}
