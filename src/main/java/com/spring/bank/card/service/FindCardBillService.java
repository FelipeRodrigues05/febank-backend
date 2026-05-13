package com.spring.bank.card.service;

import com.spring.bank.card.model.CardBill;
import com.spring.bank.card.repository.CardBillRepository;
import com.spring.bank.common.exception.CardBillNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FindCardBillService {

    private final CardBillRepository cardBillRepository;
    private final AddBillPurchaseService addBillPurchaseService;

    public CardBill getCurrentBill(String cardId) {
        LocalDate today = LocalDate.now();
        return cardBillRepository.findByCardIdAndMonthAndYear(cardId, today.getMonthValue(), today.getYear())
                .orElseGet(() -> addBillPurchaseService.createBill(cardId, today.getMonthValue(), today.getYear()));
    }

    public List<CardBill> listByCard(String cardId) {
        return cardBillRepository.findAllByCardIdOrderByYearDescMonthDesc(cardId);
    }

    public CardBill getById(Long id) {
        return cardBillRepository.findById(id).orElseThrow(() ->
                new CardBillNotFoundException("Card bill with ID " + id + " not found"));
    }
}
