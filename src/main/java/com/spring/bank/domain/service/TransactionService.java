package com.spring.bank.domain.service;

import com.spring.bank.common.config.messaging.RabbitMQConfig;
import com.spring.bank.common.exception.TransactionNotFoundException;
import com.spring.bank.domain.dto.transaction.CreateTransactionDTO;
import com.spring.bank.domain.dto.transaction.TransactionResponseDTO;
import com.spring.bank.domain.enums.transaction.TransactionStatusEnum;
import com.spring.bank.domain.model.Transaction;
import com.spring.bank.domain.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public void create(CreateTransactionDTO data) {
        Transaction transaction = new Transaction();
        transaction.setAccount(data.account());
        transaction.setType(data.type());
        transaction.setAmount(data.amount());
        transaction.setDescription(data.description() != null ? data.description() : "");
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setStatus(TransactionStatusEnum.PENDING);

        this.transactionRepository.save(transaction);

        rabbitTemplate.convertAndSend(RabbitMQConfig.TRANSACTION_QUEUE, transaction.getId());
        log.debug("Transaction queued: id={} type={} amount={}", transaction.getId(), data.type(), data.amount());
    }

    public List<TransactionResponseDTO> listByAccount(Long id) {
        List<Transaction> transactions = this.transactionRepository.findAllByAccountId(id);

        return transactions.stream()
                .map(TransactionResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void completeTransaction(Long transactionId) {
        Transaction transaction = this.transactionRepository.findById(transactionId).orElseThrow(() ->
                new TransactionNotFoundException(String.format("Transaction with ID %s not found", transactionId))
        );

        transaction.setStatus(TransactionStatusEnum.COMPLETED);
        transaction.setExecutedAt(LocalDateTime.now());

        this.transactionRepository.save(transaction);
        log.debug("Transaction completed: id={}", transactionId);
    }
}
