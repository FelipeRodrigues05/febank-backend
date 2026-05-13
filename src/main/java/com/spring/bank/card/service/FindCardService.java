package com.spring.bank.card.service;

import com.spring.bank.account.model.Account;
import com.spring.bank.card.converter.EncryptionConverter;
import com.spring.bank.card.dto.CardResponseDTO;
import com.spring.bank.card.enums.CardType;
import com.spring.bank.card.model.Card;
import com.spring.bank.card.repository.CardRepository;
import com.spring.bank.common.exception.CardNotActiveException;
import com.spring.bank.common.exception.CardNotFoundException;
import com.spring.bank.common.exception.ExpiredCardException;
import com.spring.bank.common.exception.InvalidCardTypeException;
import com.spring.bank.common.exception.InvalidCvvException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FindCardService {

    private final CardRepository cardRepository;
    private final EncryptionConverter encryptionConverter;

    public List<CardResponseDTO> listByAccount(Account account) {
        return cardRepository.findAllByAccount(account)
                .stream()
                .map(CardResponseDTO::new)
                .collect(Collectors.toList());
    }

    public Card validateForPayment(String cardNumber, String cvv, CardType cardType) {
        String encryptedNumber = encryptionConverter.encrypt(cardNumber);
        Card card = cardRepository.findByNumber(encryptedNumber).orElseThrow(() ->
                new CardNotFoundException(String.format("Card with number ending in %s not found",
                        cardNumber.substring(cardNumber.length() - 4)))
        );

        String decryptedCvv = encryptionConverter.decrypt(card.getCvv());
        if (!card.getCardStatus().isUsable()) throw new CardNotActiveException("Card is not active.");
        if (!decryptedCvv.equals(cvv)) throw new InvalidCvvException("Invalid CVV");
        if (card.getExpirationDate().isBefore(LocalDate.now())) throw new ExpiredCardException("Card is expired");
        if (cardType != null && card.getCardType() != cardType) throw new InvalidCardTypeException("Incorrect card type");

        return card;
    }
}
