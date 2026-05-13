package com.spring.bank.card.service;

import com.spring.bank.card.enums.CardBillStatus;
import com.spring.bank.card.model.Card;
import com.spring.bank.card.model.CardBill;
import com.spring.bank.card.repository.CardBillRepository;
import com.spring.bank.card.repository.CardRepository;
import com.spring.bank.common.exception.CardNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AddBillPurchaseService {

    private static final Logger log = LoggerFactory.getLogger(AddBillPurchaseService.class);

    private final CardBillRepository cardBillRepository;
    private final CardRepository cardRepository;

    @Transactional
    public CardBill addPurchase(Card card, BigDecimal amount) {
        LocalDate today = LocalDate.now();
        CardBill bill = cardBillRepository.findByCardIdAndMonthAndYear(card.getId(), today.getMonthValue(), today.getYear())
                .orElseGet(() -> createBill(card.getId(), today.getMonthValue(), today.getYear()));

        bill.setTotalAmount(bill.getTotalAmount().add(amount));
        CardBill saved = cardBillRepository.save(bill);
        log.info("Purchase added to bill: billId={} cardId={} amount={} newTotal={}", saved.getId(), card.getId(), amount, saved.getTotalAmount());
        return saved;
    }

    public CardBill createBill(String cardId, int month, int year) {
        Card card = cardRepository.findById(cardId).orElseThrow(() ->
                new CardNotFoundException("Card " + cardId + " not found"));

        CardBill bill = new CardBill();
        bill.setCard(card);
        bill.setMonth(month);
        bill.setYear(year);
        bill.setTotalAmount(BigDecimal.ZERO);
        bill.setMinimumPayment(BigDecimal.ZERO);
        bill.setStatus(CardBillStatus.OPEN);

        CardBill saved = cardBillRepository.save(bill);
        log.info("Card bill created: id={} cardId={} {}/{}", saved.getId(), cardId, month, year);
        return saved;
    }
}
