package com.spring.bank.transaction.service;

import com.spring.bank.transaction.dto.TransactionResponseDTO;
import com.spring.bank.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FindTransactionService {

    private final TransactionRepository transactionRepository;

    public List<TransactionResponseDTO> listByAccount(Long accountId) {
        return transactionRepository.findAllByAccountId(accountId)
                .stream()
                .map(TransactionResponseDTO::new)
                .collect(Collectors.toList());
    }
}
