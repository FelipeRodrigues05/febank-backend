package com.spring.bank.openfinance.controller;

import com.spring.bank.openfinance.dto.AccountDataResponseDTO;
import com.spring.bank.openfinance.dto.ConsentResponseDTO;
import com.spring.bank.openfinance.dto.CreateConsentDTO;
import com.spring.bank.openfinance.service.ConsentService;
import com.spring.bank.openfinance.service.OpenFinanceDataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/open-finance")
@RequiredArgsConstructor
public class OpenFinanceController {

    private final ConsentService consentService;
    private final OpenFinanceDataService openFinanceDataService;

    @PostMapping("/consents")
    public ResponseEntity<ConsentResponseDTO> createConsent(@Valid @RequestBody CreateConsentDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ConsentResponseDTO(consentService.create(dto)));
    }

    @PostMapping("/consents/{id}/authorise")
    public ResponseEntity<ConsentResponseDTO> authoriseConsent(
            @PathVariable String id,
            @RequestParam Long userId
    ) {
        return ResponseEntity.ok(new ConsentResponseDTO(consentService.authorise(id, userId)));
    }

    @DeleteMapping("/consents/{id}")
    public ResponseEntity<Void> revokeConsent(
            @PathVariable String id,
            @RequestParam Long userId
    ) {
        consentService.revoke(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/consents/user/{userId}")
    public ResponseEntity<List<ConsentResponseDTO>> listUserConsents(@PathVariable Long userId) {
        return ResponseEntity.ok(consentService.listByUser(userId));
    }

    @GetMapping("/data/accounts")
    public ResponseEntity<List<AccountDataResponseDTO>> getAccountData(
            @RequestParam String consentId,
            @RequestParam String clientId,
            @RequestParam Long userId
    ) {
        return ResponseEntity.ok(openFinanceDataService.getAccountData(consentId, clientId, userId));
    }
}
