package com.spring.bank.loan.controller;

import com.spring.bank.loan.dto.ApplyLoanDTO;
import com.spring.bank.loan.dto.LoanInstallmentResponseDTO;
import com.spring.bank.loan.dto.LoanResponseDTO;
import com.spring.bank.loan.dto.PayInstallmentDTO;
import com.spring.bank.loan.service.ApplyLoanService;
import com.spring.bank.loan.service.FindLoanService;
import com.spring.bank.loan.service.PayLoanInstallmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/loan")
@RequiredArgsConstructor
public class LoanController {

    private final ApplyLoanService applyLoanService;
    private final PayLoanInstallmentService payLoanInstallmentService;
    private final FindLoanService findLoanService;

    @PostMapping("/apply")
    public ResponseEntity<LoanResponseDTO> apply(@Valid @RequestBody ApplyLoanDTO body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new LoanResponseDTO(applyLoanService.apply(body)));
    }

    @PostMapping("/pay-installment")
    public ResponseEntity<LoanInstallmentResponseDTO> payInstallment(@Valid @RequestBody PayInstallmentDTO body) {
        return ResponseEntity.ok(new LoanInstallmentResponseDTO(payLoanInstallmentService.pay(body)));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<LoanResponseDTO>> listByAccount(@PathVariable Long accountId) {
        return ResponseEntity.ok(findLoanService.listByAccount(accountId));
    }

    @GetMapping("/{id}/installments")
    public ResponseEntity<List<LoanInstallmentResponseDTO>> getInstallments(@PathVariable Long id) {
        return ResponseEntity.ok(findLoanService.getInstallments(id));
    }
}
