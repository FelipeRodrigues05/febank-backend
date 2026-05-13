package com.spring.bank.card.controller;

import com.spring.bank.card.dto.CashbackSummaryDTO;
import com.spring.bank.card.service.CashbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/card/cashback")
@RequiredArgsConstructor
public class CashbackController {

    private final CashbackService cashbackService;

    @PostMapping("/{cardId}/credit")
    public ResponseEntity<Map<String, BigDecimal>> creditPending(@PathVariable String cardId) {
        BigDecimal credited = cashbackService.creditPending(cardId);
        return ResponseEntity.ok(Map.of("credited", credited));
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<CashbackSummaryDTO> getSummary(@PathVariable String cardId) {
        return ResponseEntity.ok(cashbackService.getSummary(cardId));
    }
}
