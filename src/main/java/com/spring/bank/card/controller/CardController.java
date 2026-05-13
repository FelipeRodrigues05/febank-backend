package com.spring.bank.card.controller;

import com.spring.bank.account.model.Account;
import com.spring.bank.account.service.FindAccountService;
import com.spring.bank.card.dto.CardResponseDTO;
import com.spring.bank.card.dto.CreateCardDTO;
import com.spring.bank.card.dto.PurchaseDTO;
import com.spring.bank.card.model.Card;
import com.spring.bank.card.service.CardPaymentService;
import com.spring.bank.card.service.CreateCardService;
import com.spring.bank.card.service.FindCardService;
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

    private final CreateCardService createCardService;
    private final FindCardService findCardService;
    private final CardPaymentService cardPaymentService;
    private final FindAccountService findAccountService;

    @PostMapping
    public ResponseEntity<CardResponseDTO> createCard(@Valid @RequestBody CreateCardDTO body) {
        Card card = createCardService.create(body.accountId(), body.cardType());
        return ResponseEntity.status(HttpStatus.CREATED).body(new CardResponseDTO(card));
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<List<CardResponseDTO>> listCards(@PathVariable Long accountId) {
        Account account = findAccountService.getById(accountId);
        return ResponseEntity.ok(findCardService.listByAccount(account));
    }

    @PostMapping("/purchase")
    public ResponseEntity<Void> purchase(@Valid @RequestBody PurchaseDTO body) {
        cardPaymentService.processPayment(body);
        return ResponseEntity.ok().build();
    }
}
