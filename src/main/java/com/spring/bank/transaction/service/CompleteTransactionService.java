package com.spring.bank.transaction.service;

import com.spring.bank.common.exception.TransactionNotFoundException;
import com.spring.bank.transaction.enums.TransactionStatusEnum;
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
public class CompleteTransactionService {

    private static final Logger log = LoggerFactory.getLogger(CompleteTransactionService.class);

    private final TransactionRepository transactionRepository;

    @Transactional
    public void complete(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId).orElseThrow(() ->
                new TransactionNotFoundException(String.format("Transaction with ID %s not found", transactionId))
        );

        transaction.setStatus(TransactionStatusEnum.COMPLETED);
        transaction.setExecutedAt(LocalDateTime.now());

        transactionRepository.save(transaction);
        log.debug("Transaction completed: id={}", transactionId);
    }
}
