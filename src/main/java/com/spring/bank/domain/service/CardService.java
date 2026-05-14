package com.spring.bank.domain.service;

import com.spring.bank.common.exception.*;
import com.spring.bank.common.utils.CardNumberGenerator;
import com.spring.bank.domain.dto.card.CardResponseDTO;
import com.spring.bank.domain.dto.card.PurchaseDTO;
import com.spring.bank.domain.dto.transaction.CreateTransactionDTO;
import com.spring.bank.domain.enums.account.AccountStatusEnum;
import com.spring.bank.domain.enums.card.CardStatus;
import com.spring.bank.domain.enums.card.CardType;
import com.spring.bank.domain.enums.transaction.TransactionTypeEnum;
import com.spring.bank.domain.model.Account;
import com.spring.bank.domain.model.Card;
import com.spring.bank.domain.repository.CardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardService {

    private static final Logger log = LoggerFactory.getLogger(CardService.class);

    private final CardRepository cardRepository;
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final CardBillService cardBillService;

    @Value("${app.card.credit.default-limit:1000}")
    private BigDecimal creditDefaultLimit;

    @Transactional
    public Card createCard(Long accountId, CardType cardType) {
        Account account = this.validateAccount(accountId);

        Card card = new Card();
        card.setAccount(account);
        card.setNumber(CardNumberGenerator.generateCardNumber());
        card.setCardType(cardType);
        card.setCvv(CardNumberGenerator.generateCVV());
        card.setExpirationDate(CardNumberGenerator.generateExpirationDate());
        card.setCardStatus(CardStatus.ACTIVE);

        if (cardType == CardType.CREDIT) {
            card.setLimitAvailable(creditDefaultLimit);
            card.setUsedLimit(BigDecimal.ZERO);
        }

        Card saved = this.cardRepository.save(card);
        log.info("Card created: id={} type={} accountId={}", saved.getId(), cardType, accountId);
        return saved;
    }

    public List<CardResponseDTO> listCards(Long accountId) {
        Account account = this.validateAccount(accountId);

        List<Card> cards = this.cardRepository.findAllByAccount(account);

        return cards.stream()
                .map(CardResponseDTO::new)
                .collect(Collectors.toList());
    }

    public Card blockCard(Card card) {
        card.setCardStatus(CardStatus.BLOCKED);
        Card saved = this.cardRepository.save(card);
        log.info("Card blocked: id={}", card.getId());
        return saved;
    }

    public Card unblockCard(Card card) {
        card.setCardStatus(CardStatus.ACTIVE);
        Card saved = this.cardRepository.save(card);
        log.info("Card unblocked: id={}", card.getId());
        return saved;
    }

    @Transactional
    public void processPayment(PurchaseDTO data) {
        Card card = this.validateCard(data.cardNumber(), data.cvv(), data.cardType());

        Account account = card.getAccount();

        if (account.getStatus() != AccountStatusEnum.ACTIVE) {
            throw new InvalidAccountException("Account linked to this card is not active.");
        }

        switch (card.getCardType()) {
            case DEBIT -> {
                if (account.getBalance().compareTo(data.amount()) < 0) {
                    throw new InsufficientFundsException("Insufficient funds.");
                }
                accountService.subtractFunds(account.getId(), data.amount());
                transactionService.create(new CreateTransactionDTO(account, TransactionTypeEnum.DEBIT, data.amount(), "DEBIT CARD PURCHASE"));
            }
            case CREDIT -> {
                if (card.getLimitAvailable().compareTo(data.amount()) < 0) {
                    throw new InsufficientFundsException("Insufficient credit limit.");
                }
                card.setLimitAvailable(card.getLimitAvailable().subtract(data.amount()));
                card.setUsedLimit(card.getUsedLimit().add(data.amount()));
                cardRepository.save(card);
                cardBillService.addPurchaseToBill(card, data.amount());
            }
            default -> throw new UnsupportedOperationException("Card type not supported");
        }
        log.info("Payment processed: cardType={} amount={} accountId={}", card.getCardType(), data.amount(), account.getId());
    }

    private Card validateCard(String cardNumber, String cvv, CardType cardType) {
        Card card = this.cardRepository.findByNumber(cardNumber).orElseThrow(() ->
                new CardNotFoundException(String.format("Card with number ending in %s not found", cardNumber.substring(cardNumber.length() - 4)))
        );

        if (!card.getCardStatus().isUsable()) throw new CardNotActiveException("Card is not active.");

        if (!card.getCvv().equals(cvv)) throw new InvalidCvvException("Invalid CVV");

        if (card.getExpirationDate().isBefore(LocalDate.now())) throw new ExpiredCardException("Card is expired");

        if (cardType != null && card.getCardType() != cardType)
            throw new InvalidCardTypeException("Incorrect card type");

        return card;
    }

    private Account validateAccount(Long accountId) {
        Account account = this.accountService.getById(accountId);

        if (account.getStatus() == AccountStatusEnum.CLOSED || account.getStatus() == AccountStatusEnum.BLOCKED) {
            throw new InvalidAccountException("Your account is not eligible for cards.");
        }

        return account;
    }
}
