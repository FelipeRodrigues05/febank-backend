package com.spring.bank.domain.http.controller;

import com.spring.bank.domain.dto.savingsbox.CreateSavingsBoxDTO;
import com.spring.bank.domain.dto.savingsbox.SavingsBoxDepositDTO;
import com.spring.bank.domain.dto.savingsbox.SavingsBoxResponseDTO;
import com.spring.bank.domain.dto.savingsbox.SavingsBoxWithdrawDTO;
import com.spring.bank.domain.model.SavingsBox;
import com.spring.bank.domain.service.SavingsBoxService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/savings-box")
@RequiredArgsConstructor
public class SavingsBoxController {

    private final SavingsBoxService savingsBoxService;

    @PostMapping
    public ResponseEntity<SavingsBoxResponseDTO> create(@Valid @RequestBody CreateSavingsBoxDTO body) {
        SavingsBox box = savingsBoxService.create(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(new SavingsBoxResponseDTO(box));
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<SavingsBoxResponseDTO> deposit(
            @PathVariable Long id,
            @Valid @RequestBody SavingsBoxDepositDTO body) {
        SavingsBox box = savingsBoxService.deposit(id, body);
        return ResponseEntity.ok(new SavingsBoxResponseDTO(box));
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<SavingsBoxResponseDTO> withdraw(
            @PathVariable Long id,
            @Valid @RequestBody SavingsBoxWithdrawDTO body) {
        SavingsBox box = savingsBoxService.withdraw(id, body);
        return ResponseEntity.ok(new SavingsBoxResponseDTO(box));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<SavingsBoxResponseDTO>> listByAccount(@PathVariable Long accountId) {
        List<SavingsBoxResponseDTO> response = savingsBoxService.listByAccount(accountId)
                .stream()
                .map(SavingsBoxResponseDTO::new)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SavingsBoxResponseDTO> getById(@PathVariable Long id) {
        SavingsBox box = savingsBoxService.getById(id);
        return ResponseEntity.ok(new SavingsBoxResponseDTO(box));
    }
}
