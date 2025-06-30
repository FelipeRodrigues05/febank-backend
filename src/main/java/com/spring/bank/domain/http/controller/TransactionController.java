package com.spring.bank.domain.http.controller;

import com.spring.bank.domain.model.Transaction;
import com.spring.bank.domain.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(name = "transaction", path = "transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/account/{id}")
    public ResponseEntity<List<Transaction>> listByAccount(@RequestParam UUID accountId) {
        List<Transaction> transactionList = this.transactionService.listByAccount(accountId);

        return ResponseEntity.status(HttpStatus.OK).body(transactionList);
    }
}
