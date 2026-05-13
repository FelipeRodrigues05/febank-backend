package com.spring.bank.boleto.dto;

import com.spring.bank.boleto.enums.BoletoStatus;
import com.spring.bank.boleto.model.Boleto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record BoletoResponseDTO(
        Long id,
        Long issuerAccountId,
        BigDecimal amount,
        String description,
        String boletoCode,
        BoletoStatus status,
        LocalDate dueDate,
        LocalDateTime paidAt,
        LocalDateTime createdAt
) {
    public BoletoResponseDTO(Boleto boleto) {
        this(
                boleto.getId(),
                boleto.getIssuerAccountId(),
                boleto.getAmount(),
                boleto.getDescription(),
                boleto.getBoletoCode(),
                boleto.getStatus(),
                boleto.getDueDate(),
                boleto.getPaidAt(),
                boleto.getCreatedAt()
        );
    }
}
