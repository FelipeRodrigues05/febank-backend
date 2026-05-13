package com.spring.bank.transaction.controller;

import com.spring.bank.transaction.dto.PagedTransactionResponseDTO;
import com.spring.bank.transaction.dto.TransactionFilterDTO;
import com.spring.bank.transaction.enums.TransactionStatusEnum;
import com.spring.bank.transaction.enums.TransactionTypeEnum;
import com.spring.bank.transaction.service.FilterTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionQueryController {

    private final FilterTransactionService filterTransactionService;

    @GetMapping("/filter")
    public ResponseEntity<PagedTransactionResponseDTO> filter(
            @RequestParam(required = false) Long accountId,
            @RequestParam(required = false) TransactionTypeEnum type,
            @RequestParam(required = false) TransactionStatusEnum status,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) String description,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        TransactionFilterDTO filter = new TransactionFilterDTO(
                accountId, type, status, from, to, minAmount, maxAmount, description
        );
        return ResponseEntity.ok(filterTransactionService.filter(filter, page, size));
    }
}
