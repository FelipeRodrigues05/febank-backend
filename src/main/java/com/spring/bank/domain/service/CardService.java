package com.spring.bank.domain.service;

import com.spring.bank.common.exception.*;
import com.spring.bank.common.utils.CardNumberGenerator;
import com.spring.bank.domain.dto.card.CardResponseDTO;
import com.spring.bank.domain.dto.card.PurchaseDTO;
import com.spring.bank.domain.dto.transaction.TransactionResponseDTO;
import com.spring.bank.domain.enums.account.AccountStatusEnum;
import com.spring.bank.domain.enums.card.CardStatus;
import com.spring.bank.domain.enums.card.CardType;
import com.spring.bank.domain.model.Account;
import com.spring.bank.domain.model.Card;
import com.spring.bank.domain.model.Transaction;
import com.spring.bank.domain.repository.CardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final AccountService accountService;

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
            card.setLimitAvailable(BigDecimal.valueOf(1000));
        }

        return this.cardRepository.save(card);
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

        return this.cardRepository.save(card);
    }

    public Card unblockCard(Card card) {
        card.setCardStatus(CardStatus.ACTIVE);

        return this.cardRepository.save(card);
    }

    public void processPayment(PurchaseDTO data) {
        Card card = this.validateCard(data.cardNumber(), data.cvv(), data.cardType());

        switch (card.getCardType()) {
            case DEBIT -> this.validateDebitPurchase(card, data.amount());
            case CREDIT -> this.validateCreditPurchase(card, data.amount());
            default -> throw new UnsupportedOperationException("Card type not supported");
        }
    }

    private Card validateCard(String cardNumber, String cvv, CardType cardType) {
        Card card = this.cardRepository.findByNumber(cardNumber).orElseThrow(() -> new CardNotFoundException("Not Found"));

        if (card.getCardStatus().isUsable()) throw new CardNotActiveException("Card is not active.");

        if (!card.getCvv().equals(cvv)) throw new InvalidCvvException("CVV Invalid");

        if (card.getExpirationDate().isBefore(LocalDate.now())) throw new ExpiredCardException("Card expired");

        if (cardType != null && card.getCardType() != cardType)
            throw new InvalidCardTypeException("Incorrect card type");

        return card;
    }

    private void validateDebitPurchase(Card card, BigDecimal amount) {
        if (card.getAccount().getBalance().compareTo(amount) < 0)
            throw new InsufficientFundsException("Insufficient funds.");
    }

    private void validateCreditPurchase(Card card, BigDecimal amount) {
        if (card.getLimitAvailable().compareTo(amount) < 0)
            throw new InsufficientFundsException("Insufficient funds.");
    }

    private Account validateAccount(Long accountId) {
        Account account = this.accountService.getById(accountId);

        if (account.getStatus() == AccountStatusEnum.CLOSED || account.getStatus() == AccountStatusEnum.BLOCKED) {
            throw new InvalidAccountException("Your account is not eligible for cards.");
        }

        return account;
    }

}
