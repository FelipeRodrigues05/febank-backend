package com.spring.bank.domain.http.controller;

import com.spring.bank.domain.dto.card.CardResponseDTO;
import com.spring.bank.domain.dto.card.CreateCardDTO;
import com.spring.bank.domain.dto.card.PurchaseDTO;
import com.spring.bank.domain.model.Card;
import com.spring.bank.domain.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/card")
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;

    @PostMapping
    public ResponseEntity<CardResponseDTO> createCard(@Valid @RequestBody CreateCardDTO body) {
        Card card = this.cardService.createCard(body.accountId(), body.cardType());
        return ResponseEntity.status(HttpStatus.CREATED).body(new CardResponseDTO(card));
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<List<CardResponseDTO>> listCards(@PathVariable Long accountId) {
        List<CardResponseDTO> cardList = this.cardService.listCards(accountId);

        return ResponseEntity.ok(cardList);
    }

    @PostMapping("/purchase")
    public ResponseEntity<Void> purchase(@Valid @RequestBody PurchaseDTO body) {
        this.cardService.processPayment(body);

        return ResponseEntity.ok().build();
    }
}
