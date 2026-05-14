package com.spring.bank.card.controller;

import com.spring.bank.card.dto.CardResponseDTO;
import com.spring.bank.card.dto.CreateVirtualCardDTO;
import com.spring.bank.card.dto.InstallmentPlanResponseDTO;
import com.spring.bank.card.dto.InstallmentPurchaseDTO;
import com.spring.bank.card.dto.LimitIncreaseResponseDTO;
import com.spring.bank.card.dto.RequestLimitIncreaseDTO;
import com.spring.bank.card.dto.VirtualCardResponseDTO;
import com.spring.bank.card.model.Card;
import com.spring.bank.card.model.LimitIncreaseRequest;
import com.spring.bank.card.model.VirtualCard;
import com.spring.bank.card.repository.VirtualCardRepository;
import com.spring.bank.card.service.CreateCardService;
import com.spring.bank.card.service.FindCardByIdService;
import com.spring.bank.card.service.InstallmentService;
import com.spring.bank.card.service.LimitIncreaseService;
import com.spring.bank.card.service.VirtualCardService;
import com.spring.bank.common.exception.CardNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/card")
@RequiredArgsConstructor
public class CardManagementController {

    private final CreateCardService createCardService;
    private final FindCardByIdService findCardByIdService;
    private final VirtualCardService virtualCardService;
    private final VirtualCardRepository virtualCardRepository;
    private final InstallmentService installmentService;
    private final LimitIncreaseService limitIncreaseService;

    @PostMapping("/{cardId}/block")
    public ResponseEntity<CardResponseDTO> block(@PathVariable String cardId) {
        Card card = findCardByIdService.getById(cardId);
        Card blocked = createCardService.block(card);
        return ResponseEntity.ok(new CardResponseDTO(blocked));
    }

    @PostMapping("/{cardId}/unblock")
    public ResponseEntity<CardResponseDTO> unblock(@PathVariable String cardId) {
        Card card = findCardByIdService.getById(cardId);
        Card unblocked = createCardService.unblock(card);
        return ResponseEntity.ok(new CardResponseDTO(unblocked));
    }

    @PostMapping("/virtual")
    public ResponseEntity<CardResponseDTO> createVirtual(@Valid @RequestBody CreateVirtualCardDTO body) {
        Card card = virtualCardService.createVirtual(body.accountId());
        return ResponseEntity.status(HttpStatus.CREATED).body(new CardResponseDTO(card));
    }

    @GetMapping("/{cardId}/virtual")
    public ResponseEntity<VirtualCardResponseDTO> getVirtualCard(@PathVariable String cardId) {
        VirtualCard virtualCard = virtualCardRepository.findByCardId(cardId).orElseThrow(() ->
                new CardNotFoundException("Virtual card not found for card " + cardId));
        return ResponseEntity.ok(new VirtualCardResponseDTO(virtualCard));
    }

    @PostMapping("/installment")
    public ResponseEntity<InstallmentPlanResponseDTO> createInstallment(@Valid @RequestBody InstallmentPurchaseDTO body) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new InstallmentPlanResponseDTO(installmentService.createInstallment(body)));
    }

    @GetMapping("/{cardId}/installments")
    public ResponseEntity<List<InstallmentPlanResponseDTO>> listInstallments(@PathVariable String cardId) {
        return ResponseEntity.ok(installmentService.listActive(cardId));
    }

    @PostMapping("/limit-increase")
    public ResponseEntity<LimitIncreaseResponseDTO> requestLimitIncrease(@Valid @RequestBody RequestLimitIncreaseDTO body) {
        LimitIncreaseRequest limitRequest = limitIncreaseService.request(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(new LimitIncreaseResponseDTO(limitRequest));
    }

    @GetMapping("/{cardId}/limit-requests")
    public ResponseEntity<List<LimitIncreaseResponseDTO>> listLimitRequests(@PathVariable String cardId) {
        return ResponseEntity.ok(limitIncreaseService.listByCard(cardId));
    }
}
