package com.spring.bank.card.service;

import com.spring.bank.account.service.AccountFundsService;
import com.spring.bank.card.dto.CashbackSummaryDTO;
import com.spring.bank.card.model.Card;
import com.spring.bank.card.model.CashbackRecord;
import com.spring.bank.card.repository.CashbackRepository;
import com.spring.bank.transaction.dto.CreateTransactionDTO;
import com.spring.bank.transaction.enums.TransactionTypeEnum;
import com.spring.bank.transaction.service.CreateTransactionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CashbackService {

    private static final Logger log = LoggerFactory.getLogger(CashbackService.class);
    private static final BigDecimal CASHBACK_RATE = new BigDecimal("0.005");

    private final CashbackRepository cashbackRepository;
    private final FindCardByIdService findCardByIdService;
    private final AccountFundsService accountFundsService;
    private final CreateTransactionService createTransactionService;

    public void recordCashback(Card card, BigDecimal purchaseAmount) {
        BigDecimal cashbackAmount = purchaseAmount.multiply(CASHBACK_RATE).setScale(2, RoundingMode.HALF_UP);

        if (cashbackAmount.compareTo(BigDecimal.ZERO) > 0) {
            CashbackRecord record = new CashbackRecord();
            record.setCard(card);
            record.setTransactionAmount(purchaseAmount);
            record.setCashbackAmount(cashbackAmount);
            record.setCredited(false);

            cashbackRepository.save(record);
            log.info("Cashback recorded: cardId={} purchaseAmount={} cashbackAmount={}", card.getId(), purchaseAmount, cashbackAmount);
        }
    }

    @Transactional
    public BigDecimal creditPending(String cardId) {
        Card card = findCardByIdService.getById(cardId);
        List<CashbackRecord> pendingRecords = cashbackRepository.findByCardIdAndCreditedFalse(cardId);

        if (pendingRecords.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = pendingRecords.stream()
                .map(CashbackRecord::getCashbackAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        accountFundsService.addFunds(card.getAccount().getId(), total);

        createTransactionService.create(new CreateTransactionDTO(
                card.getAccount(),
                TransactionTypeEnum.CREDIT,
                total,
                "CASHBACK CREDIT"
        ));

        pendingRecords.forEach(record -> record.setCredited(true));
        cashbackRepository.saveAll(pendingRecords);

        log.info("Cashback credited: cardId={} total={} records={}", cardId, total, pendingRecords.size());
        return total;
    }

    public CashbackSummaryDTO getSummary(String cardId) {
        findCardByIdService.getById(cardId);
        List<CashbackRecord> pendingRecords = cashbackRepository.findByCardIdAndCreditedFalse(cardId);

        BigDecimal pendingTotal = pendingRecords.stream()
                .map(CashbackRecord::getCashbackAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CashbackSummaryDTO(cardId, pendingTotal, new BigDecimal(pendingRecords.size()));
    }
}
