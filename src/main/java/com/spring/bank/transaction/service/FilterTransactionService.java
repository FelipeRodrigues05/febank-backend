package com.spring.bank.transaction.service;

import com.spring.bank.transaction.dto.PagedTransactionResponseDTO;
import com.spring.bank.transaction.dto.TransactionFilterDTO;
import com.spring.bank.transaction.dto.TransactionResponseDTO;
import com.spring.bank.transaction.model.Transaction;
import com.spring.bank.transaction.repository.TransactionQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilterTransactionService {

    private final TransactionQueryRepository transactionQueryRepository;
    private final TransactionCategorizationService transactionCategorizationService;

    public PagedTransactionResponseDTO filter(TransactionFilterDTO filter, int page, int size) {
        List<Transaction> transactions = transactionQueryRepository.findWithFilters(
                filter.accountId(),
                filter.type(),
                filter.status(),
                filter.from(),
                filter.to(),
                filter.minAmount(),
                filter.maxAmount(),
                filter.description(),
                page,
                size
        );

        long total = transactionQueryRepository.countWithFilters(
                filter.accountId(),
                filter.type(),
                filter.status(),
                filter.from(),
                filter.to(),
                filter.minAmount(),
                filter.maxAmount(),
                filter.description()
        );

        List<TransactionResponseDTO> responseDTOs = transactions.stream()
                .map(TransactionResponseDTO::new)
                .toList();

        int totalPages = (int) Math.ceil((double) total / size);

        return new PagedTransactionResponseDTO(responseDTOs, page, size, total, totalPages);
    }
}
