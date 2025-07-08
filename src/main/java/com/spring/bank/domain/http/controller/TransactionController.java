package com.spring.bank.domain.http.controller;

import com.spring.bank.domain.dto.transaction.TransactionResponseDTO;
import com.spring.bank.domain.model.Transaction;
import com.spring.bank.domain.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(name = "transaction", path = "transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/account/{id}")
    public ResponseEntity<List<TransactionResponseDTO>> listByAccount(@PathVariable("id") Long accountId) {
        List<TransactionResponseDTO> transactionList = this.transactionService.listByAccount(accountId);

        return ResponseEntity.status(HttpStatus.OK).body(transactionList);
    }
}
