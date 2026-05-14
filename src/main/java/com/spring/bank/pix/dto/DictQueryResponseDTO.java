package com.spring.bank.pix.dto;

public record DictQueryResponseDTO(
        String key,
        String keyType,
        String ownerName,
        String institution,
        Long accountId,
        String accountNumber
) {}
