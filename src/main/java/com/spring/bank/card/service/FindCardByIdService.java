package com.spring.bank.card.service;

import com.spring.bank.card.model.Card;
import com.spring.bank.card.repository.CardRepository;
import com.spring.bank.common.exception.CardNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindCardByIdService {

    private final CardRepository cardRepository;

    public Card getById(String cardId) {
        return cardRepository.findById(cardId).orElseThrow(() ->
                new CardNotFoundException("Card " + cardId + " not found"));
    }
}
