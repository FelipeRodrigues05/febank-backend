package com.spring.bank.transaction.dto;

import java.util.List;

public record PagedTransactionResponseDTO(
        List<TransactionResponseDTO> transactions,
        int page,
        int size,
        long totalElements,
        int totalPages
) {}
