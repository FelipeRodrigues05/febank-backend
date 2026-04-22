package com.spring.bank.domain.http.controller;

import com.spring.bank.domain.dto.cardbill.CardBillResponseDTO;
import com.spring.bank.domain.dto.cardbill.PayBillDTO;
import com.spring.bank.domain.service.CardBillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/card-bill")
@RequiredArgsConstructor
public class CardBillController {

    private final CardBillService cardBillService;

    @GetMapping("/{cardId}/current")
    public ResponseEntity<CardBillResponseDTO> getCurrentBill(@PathVariable String cardId) {
        return ResponseEntity.ok(new CardBillResponseDTO(cardBillService.getCurrentBill(cardId)));
    }

    @GetMapping("/{cardId}/history")
    public ResponseEntity<List<CardBillResponseDTO>> getHistory(@PathVariable String cardId) {
        List<CardBillResponseDTO> bills = cardBillService.listByCard(cardId)
                .stream()
                .map(CardBillResponseDTO::new)
                .toList();
        return ResponseEntity.ok(bills);
    }

    @PostMapping("/{billId}/pay")
    public ResponseEntity<CardBillResponseDTO> payBill(
            @PathVariable Long billId,
            @Valid @RequestBody PayBillDTO body) {
        return ResponseEntity.ok(new CardBillResponseDTO(cardBillService.payBill(billId, body.accountId())));
    }
}
