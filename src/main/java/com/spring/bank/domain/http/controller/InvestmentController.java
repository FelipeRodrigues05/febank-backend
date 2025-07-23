package com.spring.bank.domain.http.controller;

import com.spring.bank.domain.dto.investment.ApplyInvestmentDTO;
import com.spring.bank.domain.dto.investment.InvestmentResponseDTO;
import com.spring.bank.domain.model.Account;
import com.spring.bank.domain.service.InvestmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/investment")
@RequiredArgsConstructor
public class InvestmentController {

    private final InvestmentService investmentService;

    @PostMapping("/apply")
    public ResponseEntity<InvestmentResponseDTO> apply(@Valid @RequestBody ApplyInvestmentDTO data) {
        Account investmentAccount = this.investmentService.apply(data);

        return ResponseEntity.status(HttpStatus.OK).body(new InvestmentResponseDTO(investmentAccount));
    }
}
