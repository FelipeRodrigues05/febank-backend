package com.spring.bank.card.service;

import com.spring.bank.account.enums.AccountStatusEnum;
import com.spring.bank.account.model.Account;
import com.spring.bank.account.service.FindAccountService;
import com.spring.bank.card.enums.CardStatus;
import com.spring.bank.card.enums.CardType;
import com.spring.bank.card.model.Card;
import com.spring.bank.card.model.VirtualCard;
import com.spring.bank.card.repository.CardRepository;
import com.spring.bank.card.repository.VirtualCardRepository;
import com.spring.bank.common.exception.InvalidAccountException;
import com.spring.bank.common.utils.CardNumberGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class VirtualCardService {

    private static final Logger log = LoggerFactory.getLogger(VirtualCardService.class);
    private static final int VIRTUAL_CARD_EXPIRY_YEARS = 1;

    private final CardRepository cardRepository;
    private final VirtualCardRepository virtualCardRepository;
    private final FindAccountService findAccountService;

    @Value("${app.card.credit.default-limit:1000}")
    private BigDecimal creditDefaultLimit;

    @Transactional
    public Card createVirtual(Long accountId) {
        Account account = findAccountService.getById(accountId);

        if (account.getStatus() != AccountStatusEnum.ACTIVE) {
            throw new InvalidAccountException("Account must be active to create a virtual card");
        }

        Card card = new Card();
        card.setAccount(account);
        card.setNumber(CardNumberGenerator.generateCardNumber());
        card.setCvv(CardNumberGenerator.generateCVV());
        card.setExpirationDate(LocalDate.now().plusYears(VIRTUAL_CARD_EXPIRY_YEARS));
        card.setCardType(CardType.CREDIT);
        card.setCardStatus(CardStatus.ACTIVE);
        card.setLimitAvailable(creditDefaultLimit);
        card.setUsedLimit(BigDecimal.ZERO);

        Card savedCard = cardRepository.save(card);

        VirtualCard virtualCard = new VirtualCard();
        virtualCard.setCard(savedCard);
        virtualCard.setExpiresAt(savedCard.getExpirationDate());
        virtualCard.setActive(true);

        virtualCardRepository.save(virtualCard);

        log.info("Virtual card created: cardId={} accountId={}", savedCard.getId(), accountId);
        return savedCard;
    }
}
