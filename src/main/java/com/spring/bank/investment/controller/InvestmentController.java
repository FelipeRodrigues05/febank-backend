package com.spring.bank.investment.controller;

import com.spring.bank.account.model.Account;
import com.spring.bank.investment.dto.ApplyInvestmentDTO;
import com.spring.bank.investment.dto.InvestmentResponseDTO;
import com.spring.bank.investment.dto.RedeemInvestmentDTO;
import com.spring.bank.investment.service.ApplyInvestmentService;
import com.spring.bank.investment.service.RedeemInvestmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/investment")
@RequiredArgsConstructor
public class InvestmentController {

    private final ApplyInvestmentService applyInvestmentService;
    private final RedeemInvestmentService redeemInvestmentService;

    @PostMapping("/apply")
    public ResponseEntity<InvestmentResponseDTO> apply(@Valid @RequestBody ApplyInvestmentDTO data) {
        Account account = applyInvestmentService.apply(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(new InvestmentResponseDTO(account));
    }

    @PostMapping("/redeem")
    public ResponseEntity<InvestmentResponseDTO> redeem(@Valid @RequestBody RedeemInvestmentDTO data) {
        Account account = redeemInvestmentService.redeem(data);
        return ResponseEntity.ok(new InvestmentResponseDTO(account));
    }
}
