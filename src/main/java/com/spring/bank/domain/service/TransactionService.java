package com.spring.bank.domain.service;

import com.spring.bank.domain.dto.transaction.CreateTransactionDTO;
import com.spring.bank.domain.model.Transaction;
import com.spring.bank.domain.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private TransactionRepository transactionRepository;

    @Transactional
    public void create(CreateTransactionDTO data) {
        Transaction transaction = new Transaction();
        transaction.setAccount(data.account());
        transaction.setType(data.type());
        transaction.setAmount(data.amount());
        transaction.setDescription(data.description() != null ? data.description() : "");
        transaction.setCreatedAt(LocalDateTime.now());

        this.transactionRepository.save(transaction);
    }

    public List<Transaction> listByAccount(UUID id) {
        return this.transactionRepository.findAllByAccountId(id);
    }
}
