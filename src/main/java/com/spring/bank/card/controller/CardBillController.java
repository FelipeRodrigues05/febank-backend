package com.spring.bank.card.controller;

import com.spring.bank.card.dto.CardBillResponseDTO;
import com.spring.bank.card.dto.PayBillDTO;
import com.spring.bank.card.service.FindCardBillService;
import com.spring.bank.card.service.PayCardBillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/card-bill")
@RequiredArgsConstructor
public class CardBillController {

    private final FindCardBillService findCardBillService;
    private final PayCardBillService payCardBillService;

    @GetMapping("/{cardId}/current")
    public ResponseEntity<CardBillResponseDTO> getCurrentBill(@PathVariable String cardId) {
        return ResponseEntity.ok(new CardBillResponseDTO(findCardBillService.getCurrentBill(cardId)));
    }

    @GetMapping("/{cardId}/history")
    public ResponseEntity<List<CardBillResponseDTO>> getHistory(@PathVariable String cardId) {
        List<CardBillResponseDTO> bills = findCardBillService.listByCard(cardId)
                .stream()
                .map(CardBillResponseDTO::new)
                .toList();
        return ResponseEntity.ok(bills);
    }

    @PostMapping("/{billId}/pay")
    public ResponseEntity<CardBillResponseDTO> payBill(
            @PathVariable Long billId,
            @Valid @RequestBody PayBillDTO body) {
        return ResponseEntity.ok(new CardBillResponseDTO(payCardBillService.pay(billId, body.accountId())));
    }
}
