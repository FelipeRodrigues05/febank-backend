package com.spring.bank.card.service;

import com.spring.bank.account.enums.AccountStatusEnum;
import com.spring.bank.account.model.Account;
import com.spring.bank.account.service.FindAccountService;
import com.spring.bank.card.converter.EncryptionConverter;
import com.spring.bank.card.enums.CardStatus;
import com.spring.bank.card.enums.CardType;
import com.spring.bank.card.model.Card;
import com.spring.bank.card.repository.CardRepository;
import com.spring.bank.common.exception.InvalidAccountException;
import com.spring.bank.common.utils.CardNumberGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CreateCardService {

    private static final Logger log = LoggerFactory.getLogger(CreateCardService.class);

    private final CardRepository cardRepository;
    private final FindAccountService findAccountService;
    private final EncryptionConverter encryptionConverter;

    @Value("${app.card.credit.default-limit:1000}")
    private BigDecimal creditDefaultLimit;

    @Transactional
    public Card create(Long accountId, CardType cardType) {
        Account account = findAccountService.getById(accountId);

        if (account.getStatus() == AccountStatusEnum.CLOSED || account.getStatus() == AccountStatusEnum.BLOCKED) {
            throw new InvalidAccountException("Your account is not eligible for cards.");
        }

        Card card = new Card();
        card.setAccount(account);
        card.setNumber(encryptionConverter.encrypt(CardNumberGenerator.generateCardNumber()));
        card.setCardType(cardType);
        card.setCvv(encryptionConverter.encrypt(CardNumberGenerator.generateCVV()));
        card.setExpirationDate(CardNumberGenerator.generateExpirationDate());
        card.setCardStatus(CardStatus.ACTIVE);

        if (cardType == CardType.CREDIT) {
            card.setLimitAvailable(creditDefaultLimit);
            card.setUsedLimit(BigDecimal.ZERO);
        }

        Card saved = cardRepository.save(card);
        log.info("Card created: id={} type={} accountId={}", saved.getId(), cardType, accountId);
        return saved;
    }

    public Card block(Card card) {
        card.setCardStatus(CardStatus.BLOCKED);
        Card saved = cardRepository.save(card);
        log.info("Card blocked: id={}", card.getId());
        return saved;
    }

    public Card unblock(Card card) {
        card.setCardStatus(CardStatus.ACTIVE);
        Card saved = cardRepository.save(card);
        log.info("Card unblocked: id={}", card.getId());
        return saved;
    }
}
