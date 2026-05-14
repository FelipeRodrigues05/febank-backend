package com.spring.bank.compliance.controller;

import com.spring.bank.compliance.dto.KycResponseDTO;
import com.spring.bank.compliance.dto.KycSubmitDTO;
import com.spring.bank.compliance.model.AmlFlag;
import com.spring.bank.compliance.service.AmlService;
import com.spring.bank.compliance.service.CreditScoreService;
import com.spring.bank.compliance.service.KycService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/kyc")
@RequiredArgsConstructor
public class KycController {

    private final KycService kycService;
    private final AmlService amlService;
    private final CreditScoreService creditScoreService;

    @PostMapping("/submit")
    public ResponseEntity<KycResponseDTO> submit(@RequestBody @Valid KycSubmitDTO body) {
        KycResponseDTO response = new KycResponseDTO(kycService.submit(body.userId(), body.document()));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<KycResponseDTO> getStatus(@PathVariable Long userId) {
        return ResponseEntity.ok(new KycResponseDTO(kycService.getStatus(userId)));
    }

    @GetMapping("/aml/flags")
    public ResponseEntity<List<AmlFlag>> listPendingFlags() {
        return ResponseEntity.ok(amlService.listPendingFlags());
    }

    @PatchMapping("/aml/flags/{flagId}/clear")
    public ResponseEntity<Void> clearFlag(@PathVariable Long flagId) {
        amlService.clearFlag(flagId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/aml/flags/{flagId}/report")
    public ResponseEntity<Void> reportFlag(@PathVariable Long flagId) {
        amlService.reportFlag(flagId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/credit-score/{userId}")
    public ResponseEntity<Map<String, Integer>> getCreditScore(@PathVariable Long userId) {
        int score = creditScoreService.calculate(userId);
        return ResponseEntity.ok(Map.of("score", score));
    }
}
