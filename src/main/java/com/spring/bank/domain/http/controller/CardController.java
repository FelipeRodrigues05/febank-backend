package com.spring.bank.domain.http.controller;

import com.spring.bank.domain.dto.card.CardResponseDTO;
import com.spring.bank.domain.enums.card.CardType;
import com.spring.bank.domain.model.Card;
import com.spring.bank.domain.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(name = "card", path = "/card")
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;

    @PostMapping("/create/{accountId}")
    public ResponseEntity<CardResponseDTO> createCard(@Valid @RequestBody CardType cardType, @PathVariable Long accountId) {
        Card card = this.cardService.createCard(accountId, cardType);

        return ResponseEntity.status(HttpStatus.OK).body(new CardResponseDTO(card));
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<List<CardResponseDTO>> listCards(@Valid @PathVariable Long accountId) {
        List<CardResponseDTO> cardList = this.cardService.listCards(accountId);

        return ResponseEntity.status(HttpStatus.OK).body(cardList);
    }
}
