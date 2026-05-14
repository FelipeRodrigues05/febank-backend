package com.spring.bank.card.service;

import com.spring.bank.account.enums.AccountStatusEnum;
import com.spring.bank.account.model.Account;
import com.spring.bank.account.service.AccountFundsService;
import com.spring.bank.card.dto.PurchaseDTO;
import com.spring.bank.card.model.Card;
import com.spring.bank.card.repository.CardRepository;
import com.spring.bank.common.exception.InsufficientFundsException;
import com.spring.bank.common.exception.InvalidAccountException;
import com.spring.bank.transaction.dto.CreateTransactionDTO;
import com.spring.bank.transaction.enums.TransactionTypeEnum;
import com.spring.bank.transaction.service.CreateTransactionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardPaymentService {

    private static final Logger log = LoggerFactory.getLogger(CardPaymentService.class);

    private final CardRepository cardRepository;
    private final AccountFundsService accountFundsService;
    private final CreateTransactionService createTransactionService;
    private final AddBillPurchaseService addBillPurchaseService;
    private final FindCardService findCardService;

    @Transactional
    public void processPayment(PurchaseDTO data) {
        Card card = findCardService.validateForPayment(data.cardNumber(), data.cvv(), data.cardType());
        Account account = card.getAccount();

        if (account.getStatus() != AccountStatusEnum.ACTIVE) {
            throw new InvalidAccountException("Account linked to this card is not active.");
        }

        switch (card.getCardType()) {
            case DEBIT -> {
                if (account.getBalance().compareTo(data.amount()) < 0) {
                    throw new InsufficientFundsException("Insufficient funds.");
                }
                accountFundsService.subtractFunds(account.getId(), data.amount());
                createTransactionService.create(new CreateTransactionDTO(account, TransactionTypeEnum.DEBIT, data.amount(), "DEBIT CARD PURCHASE"));
            }
            case CREDIT -> {
                if (card.getLimitAvailable().compareTo(data.amount()) < 0) {
                    throw new InsufficientFundsException("Insufficient credit limit.");
                }
                card.setLimitAvailable(card.getLimitAvailable().subtract(data.amount()));
                card.setUsedLimit(card.getUsedLimit().add(data.amount()));
                cardRepository.save(card);
                addBillPurchaseService.addPurchase(card, data.amount());
            }
            default -> throw new UnsupportedOperationException("Card type not supported");
        }

        log.info("Payment processed: cardType={} amount={} accountId={}", card.getCardType(), data.amount(), account.getId());
    }
}
