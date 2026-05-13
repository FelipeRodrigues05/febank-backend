package com.spring.bank.loan.dto;

import com.spring.bank.loan.model.LoanInstallment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record LoanInstallmentResponseDTO(
        Long id,
        int installmentNumber,
        BigDecimal amount,
        LocalDate dueDate,
        boolean paid,
        LocalDateTime paidAt
) {
    public LoanInstallmentResponseDTO(LoanInstallment installment) {
        this(
                installment.getId(),
                installment.getInstallmentNumber(),
                installment.getAmount(),
                installment.getDueDate(),
                installment.isPaid(),
                installment.getPaidAt()
        );
    }
}
