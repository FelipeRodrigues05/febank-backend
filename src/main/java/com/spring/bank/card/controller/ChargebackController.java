package com.spring.bank.card.controller;

import com.spring.bank.card.dto.ChargebackResponseDTO;
import com.spring.bank.card.dto.CreateChargebackDTO;
import com.spring.bank.card.model.Chargeback;
import com.spring.bank.card.repository.ChargebackRepository;
import com.spring.bank.card.service.ChargebackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/card/chargeback")
@RequiredArgsConstructor
public class ChargebackController {

    private final ChargebackService chargebackService;
    private final ChargebackRepository chargebackRepository;

    @PostMapping
    public ResponseEntity<ChargebackResponseDTO> request(@Valid @RequestBody CreateChargebackDTO body) {
        Chargeback chargeback = chargebackService.request(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ChargebackResponseDTO(chargeback));
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<List<ChargebackResponseDTO>> listByCard(@PathVariable String cardId) {
        List<ChargebackResponseDTO> result = chargebackRepository.findByCardId(cardId)
                .stream()
                .map(ChargebackResponseDTO::new)
                .toList();
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<ChargebackResponseDTO> approve(@PathVariable Long id) {
        Chargeback chargeback = chargebackService.approve(id);
        return ResponseEntity.ok(new ChargebackResponseDTO(chargeback));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<ChargebackResponseDTO> reject(@PathVariable Long id) {
        Chargeback chargeback = chargebackService.reject(id);
        return ResponseEntity.ok(new ChargebackResponseDTO(chargeback));
    }
}
