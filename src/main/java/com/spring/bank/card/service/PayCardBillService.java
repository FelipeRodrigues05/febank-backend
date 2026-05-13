package com.spring.bank.card.service;

import com.spring.bank.account.model.Account;
import com.spring.bank.account.service.AccountFundsService;
import com.spring.bank.account.service.FindAccountService;
import com.spring.bank.card.enums.CardBillStatus;
import com.spring.bank.card.model.Card;
import com.spring.bank.card.model.CardBill;
import com.spring.bank.card.repository.CardRepository;
import com.spring.bank.common.exception.BillAlreadyPaidException;
import com.spring.bank.common.exception.InsufficientFundsException;
import com.spring.bank.transaction.dto.CreateTransactionDTO;
import com.spring.bank.transaction.enums.TransactionTypeEnum;
import com.spring.bank.transaction.service.CreateTransactionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PayCardBillService {

    private static final Logger log = LoggerFactory.getLogger(PayCardBillService.class);

    private final CardRepository cardRepository;
    private final FindAccountService findAccountService;
    private final AccountFundsService accountFundsService;
    private final CreateTransactionService createTransactionService;
    private final FindCardBillService findCardBillService;

    @Transactional
    public CardBill pay(Long billId, Long checkingAccountId) {
        CardBill bill = findCardBillService.getById(billId);

        if (bill.getStatus() == CardBillStatus.PAID) {
            throw new BillAlreadyPaidException("Bill " + billId + " is already paid.");
        }
        if (bill.getStatus() == CardBillStatus.OPEN) {
            throw new IllegalStateException("Bill must be closed before payment.");
        }

        Account account = findAccountService.getById(checkingAccountId);
        if (account.getBalance().compareTo(bill.getTotalAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds to pay the bill.");
        }

        accountFundsService.subtractFunds(checkingAccountId, bill.getTotalAmount());
        createTransactionService.create(new CreateTransactionDTO(account, TransactionTypeEnum.DEBIT, bill.getTotalAmount(), "CREDIT CARD BILL PAYMENT"));

        Card card = bill.getCard();
        card.setUsedLimit(card.getUsedLimit().subtract(bill.getTotalAmount()).max(BigDecimal.ZERO));
        card.setLimitAvailable(card.getLimitAvailable().add(bill.getTotalAmount()));
        cardRepository.save(card);

        bill.setStatus(CardBillStatus.PAID);
        bill.setPaidAt(LocalDateTime.now());

        log.info("Bill paid: billId={} cardId={} amount={}", billId, card.getId(), bill.getTotalAmount());
        return bill;
    }
}
