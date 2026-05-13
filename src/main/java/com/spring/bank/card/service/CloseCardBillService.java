package com.spring.bank.card.service;

import com.spring.bank.card.enums.CardBillStatus;
import com.spring.bank.card.model.CardBill;
import com.spring.bank.card.repository.CardBillRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CloseCardBillService {

    private static final Logger log = LoggerFactory.getLogger(CloseCardBillService.class);
    private static final BigDecimal MINIMUM_PAYMENT_RATE = new BigDecimal("0.10");
    private static final BigDecimal MINIMUM_PAYMENT_FLOOR = new BigDecimal("10.00");
    private static final int DUE_DATE_DAYS_AFTER_CLOSE = 10;

    private final CardBillRepository cardBillRepository;

    @Transactional
    public void closeBills(int month, int year) {
        List<CardBill> bills = cardBillRepository.findAllByStatusAndMonthAndYear(CardBillStatus.OPEN, month, year);
        LocalDate dueDate = LocalDate.now().plusDays(DUE_DATE_DAYS_AFTER_CLOSE);

        for (CardBill bill : bills) {
            bill.setStatus(CardBillStatus.CLOSED);
            bill.setClosedAt(LocalDateTime.now());
            bill.setDueDate(dueDate);
            bill.setMinimumPayment(calculateMinimumPayment(bill.getTotalAmount()));
        }

        cardBillRepository.saveAll(bills);
        log.info("Closed {} bills for {}/{}", bills.size(), month, year);
    }

    private BigDecimal calculateMinimumPayment(BigDecimal total) {
        if (total.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO;
        BigDecimal calculated = total.multiply(MINIMUM_PAYMENT_RATE).setScale(2, RoundingMode.HALF_UP);
        return calculated.max(MINIMUM_PAYMENT_FLOOR).min(total);
    }
}
