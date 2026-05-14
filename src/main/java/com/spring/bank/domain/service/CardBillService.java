package com.spring.bank.domain.service;

import com.spring.bank.common.exception.BillAlreadyPaidException;
import com.spring.bank.common.exception.CardBillNotFoundException;
import com.spring.bank.common.exception.InsufficientFundsException;
import com.spring.bank.domain.dto.transaction.CreateTransactionDTO;
import com.spring.bank.domain.enums.card.CardBillStatus;
import com.spring.bank.domain.enums.transaction.TransactionTypeEnum;
import com.spring.bank.domain.model.Account;
import com.spring.bank.domain.model.Card;
import com.spring.bank.domain.model.CardBill;
import com.spring.bank.domain.repository.CardBillRepository;
import com.spring.bank.domain.repository.CardRepository;
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
public class CardBillService {

    private static final Logger log = LoggerFactory.getLogger(CardBillService.class);
    private static final BigDecimal MINIMUM_PAYMENT_RATE = new BigDecimal("0.10");
    private static final BigDecimal MINIMUM_PAYMENT_FLOOR = new BigDecimal("10.00");
    private static final int DUE_DATE_DAYS_AFTER_CLOSE = 10;

    private final CardBillRepository cardBillRepository;
    private final CardRepository cardRepository;
    private final AccountService accountService;
    private final TransactionService transactionService;

    public CardBill getOrCreateCurrentBill(String cardId) {
        LocalDate today = LocalDate.now();
        return cardBillRepository.findByCardIdAndMonthAndYear(cardId, today.getMonthValue(), today.getYear())
                .orElseGet(() -> createBill(cardId, today.getMonthValue(), today.getYear()));
    }

    @Transactional
    public CardBill addPurchaseToBill(Card card, BigDecimal amount) {
        CardBill bill = getOrCreateCurrentBill(card.getId());
        bill.setTotalAmount(bill.getTotalAmount().add(amount));
        CardBill saved = cardBillRepository.save(bill);
        log.info("Purchase added to bill: billId={} cardId={} amount={} newTotal={}", saved.getId(), card.getId(), amount, saved.getTotalAmount());
        return saved;
    }

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

    @Transactional
    public CardBill payBill(Long billId, Long checkingAccountId) {
        CardBill bill = getById(billId);

        if (bill.getStatus() == CardBillStatus.PAID) {
            throw new BillAlreadyPaidException("Bill " + billId + " is already paid.");
        }
        if (bill.getStatus() == CardBillStatus.OPEN) {
            throw new IllegalStateException("Bill must be closed before payment.");
        }

        Account account = accountService.getById(checkingAccountId);
        if (account.getBalance().compareTo(bill.getTotalAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds to pay the bill.");
        }

        accountService.subtractFunds(checkingAccountId, bill.getTotalAmount());
        transactionService.create(new CreateTransactionDTO(account, TransactionTypeEnum.DEBIT, bill.getTotalAmount(), "CREDIT CARD BILL PAYMENT"));

        Card card = bill.getCard();
        card.setUsedLimit(card.getUsedLimit().subtract(bill.getTotalAmount()).max(BigDecimal.ZERO));
        card.setLimitAvailable(card.getLimitAvailable().add(bill.getTotalAmount()));
        cardRepository.save(card);

        bill.setStatus(CardBillStatus.PAID);
        bill.setPaidAt(LocalDateTime.now());
        CardBill saved = cardBillRepository.save(bill);
        log.info("Bill paid: billId={} cardId={} amount={}", billId, card.getId(), bill.getTotalAmount());
        return saved;
    }

    public List<CardBill> listByCard(String cardId) {
        return cardBillRepository.findAllByCardIdOrderByYearDescMonthDesc(cardId);
    }

    public CardBill getCurrentBill(String cardId) {
        return getOrCreateCurrentBill(cardId);
    }

    public CardBill getById(Long id) {
        return cardBillRepository.findById(id).orElseThrow(() ->
                new CardBillNotFoundException("Card bill with ID " + id + " not found"));
    }

    private CardBill createBill(String cardId, int month, int year) {
        Card card = cardRepository.findById(cardId).orElseThrow(() ->
                new com.spring.bank.common.exception.CardNotFoundException("Card " + cardId + " not found"));

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

    private BigDecimal calculateMinimumPayment(BigDecimal total) {
        if (total.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO;
        BigDecimal calculated = total.multiply(MINIMUM_PAYMENT_RATE).setScale(2, RoundingMode.HALF_UP);
        return calculated.max(MINIMUM_PAYMENT_FLOOR).min(total);
    }
}
