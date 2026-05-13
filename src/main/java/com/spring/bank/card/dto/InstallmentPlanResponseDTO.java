package com.spring.bank.card.dto;

import com.spring.bank.card.model.CardInstallmentPlan;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InstallmentPlanResponseDTO(
        Long id,
        BigDecimal totalAmount,
        BigDecimal installmentAmount,
        int totalInstallments,
        int paidInstallments,
        int remainingInstallments,
        String description,
        LocalDateTime createdAt
) {
    public InstallmentPlanResponseDTO(CardInstallmentPlan plan) {
        this(
                plan.getId(),
                plan.getTotalAmount(),
                plan.getInstallmentAmount(),
                plan.getTotalInstallments(),
                plan.getPaidInstallments(),
                plan.getTotalInstallments() - plan.getPaidInstallments(),
                plan.getDescription(),
                plan.getCreatedAt()
        );
    }
}
