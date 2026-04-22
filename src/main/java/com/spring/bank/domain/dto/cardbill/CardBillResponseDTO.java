package com.spring.bank.domain.dto.cardbill;

import com.spring.bank.domain.enums.card.CardBillStatus;
import com.spring.bank.domain.model.CardBill;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record CardBillResponseDTO(
        Long id,
        String cardId,
        int month,
        int year,
        BigDecimal totalAmount,
        BigDecimal minimumPayment,
        LocalDate dueDate,
        CardBillStatus status,
        LocalDateTime closedAt,
        LocalDateTime paidAt,
        LocalDateTime createdAt
) {
    public CardBillResponseDTO(CardBill bill) {
        this(
                bill.getId(),
                bill.getCard().getId(),
                bill.getMonth(),
                bill.getYear(),
                bill.getTotalAmount(),
                bill.getMinimumPayment(),
                bill.getDueDate(),
                bill.getStatus(),
                bill.getClosedAt(),
                bill.getPaidAt(),
                bill.getCreatedAt()
        );
    }
}
