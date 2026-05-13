package com.spring.bank.loan.dto;

import com.spring.bank.loan.enums.LoanStatus;
import com.spring.bank.loan.enums.LoanType;
import com.spring.bank.loan.model.Loan;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LoanResponseDTO(
        Long id,
        Long accountId,
        LoanType type,
        BigDecimal requestedAmount,
        BigDecimal approvedAmount,
        BigDecimal interestRate,
        int totalInstallments,
        int paidInstallments,
        BigDecimal installmentAmount,
        LoanStatus status,
        int creditScore,
        LocalDateTime createdAt
) {
    public LoanResponseDTO(Loan loan) {
        this(
                loan.getId(),
                loan.getAccount().getId(),
                loan.getType(),
                loan.getRequestedAmount(),
                loan.getApprovedAmount(),
                loan.getInterestRate(),
                loan.getTotalInstallments(),
                loan.getPaidInstallments(),
                loan.getInstallmentAmount(),
                loan.getStatus(),
                loan.getCreditScoreAtApplication(),
                loan.getCreatedAt()
        );
    }
}
