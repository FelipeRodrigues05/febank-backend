package com.spring.bank.savings.controller;

import com.spring.bank.savings.dto.CreateSavingsBoxDTO;
import com.spring.bank.savings.dto.SavingsBoxDepositDTO;
import com.spring.bank.savings.dto.SavingsBoxResponseDTO;
import com.spring.bank.savings.dto.SavingsBoxWithdrawDTO;
import com.spring.bank.savings.model.SavingsBox;
import com.spring.bank.savings.service.CreateSavingsBoxService;
import com.spring.bank.savings.service.FindSavingsBoxService;
import com.spring.bank.savings.service.SavingsBoxDepositService;
import com.spring.bank.savings.service.SavingsBoxWithdrawService;
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

    private final CreateSavingsBoxService createSavingsBoxService;
    private final SavingsBoxDepositService savingsBoxDepositService;
    private final SavingsBoxWithdrawService savingsBoxWithdrawService;
    private final FindSavingsBoxService findSavingsBoxService;

    @PostMapping
    public ResponseEntity<SavingsBoxResponseDTO> create(@Valid @RequestBody CreateSavingsBoxDTO body) {
        SavingsBox box = createSavingsBoxService.create(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(new SavingsBoxResponseDTO(box));
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<SavingsBoxResponseDTO> deposit(
            @PathVariable Long id,
            @Valid @RequestBody SavingsBoxDepositDTO body) {
        return ResponseEntity.ok(new SavingsBoxResponseDTO(savingsBoxDepositService.deposit(id, body)));
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<SavingsBoxResponseDTO> withdraw(
            @PathVariable Long id,
            @Valid @RequestBody SavingsBoxWithdrawDTO body) {
        return ResponseEntity.ok(new SavingsBoxResponseDTO(savingsBoxWithdrawService.withdraw(id, body)));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<SavingsBoxResponseDTO>> listByAccount(@PathVariable Long accountId) {
        List<SavingsBoxResponseDTO> response = findSavingsBoxService.listByAccount(accountId)
                .stream()
                .map(SavingsBoxResponseDTO::new)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SavingsBoxResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new SavingsBoxResponseDTO(findSavingsBoxService.getById(id)));
    }
}
