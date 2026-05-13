package com.spring.bank.card.dto;

import com.spring.bank.card.enums.CardBillStatus;
import com.spring.bank.card.model.CardBill;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardBillResponseDTO {

    private Long id;
    private int month;
    private int year;
    private BigDecimal totalAmount;
    private BigDecimal minimumPayment;
    private LocalDate dueDate;
    private CardBillStatus status;
    private LocalDateTime closedAt;
    private LocalDateTime paidAt;

    public CardBillResponseDTO(CardBill bill) {
        this.id             = bill.getId();
        this.month          = bill.getMonth();
        this.year           = bill.getYear();
        this.totalAmount    = bill.getTotalAmount();
        this.minimumPayment = bill.getMinimumPayment();
        this.dueDate        = bill.getDueDate();
        this.status         = bill.getStatus();
        this.closedAt       = bill.getClosedAt();
        this.paidAt         = bill.getPaidAt();
    }
}
