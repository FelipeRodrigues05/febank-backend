package com.spring.bank.transaction.controller;

import com.spring.bank.transaction.dto.TransactionResponseDTO;
import com.spring.bank.transaction.service.FindTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final FindTransactionService findTransactionService;

    @GetMapping("/account/{id}")
    public ResponseEntity<List<TransactionResponseDTO>> listByAccount(@PathVariable Long id) {
        return ResponseEntity.ok(findTransactionService.listByAccount(id));
    }
}
