package com.spring.bank.account.controller;

import com.spring.bank.account.dto.AccountResponseDTO;
import com.spring.bank.account.dto.DepositDTO;
import com.spring.bank.account.dto.OpenAccountDTO;
import com.spring.bank.account.dto.WithdrawDTO;
import com.spring.bank.account.model.Account;
import com.spring.bank.account.service.AccountMovementService;
import com.spring.bank.account.service.FindAccountService;
import com.spring.bank.account.service.OpenAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final OpenAccountService openAccountService;
    private final FindAccountService findAccountService;
    private final AccountMovementService accountMovementService;

    @PostMapping("/open")
    public ResponseEntity<AccountResponseDTO> createAccount(@Valid @RequestBody OpenAccountDTO body) {
        Account account = openAccountService.open(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AccountResponseDTO(account));
    }

    @GetMapping("/{number}")
    public ResponseEntity<AccountResponseDTO> getAccount(@PathVariable String number) {
        Account account = findAccountService.getByNumber(number);
        return ResponseEntity.ok(new AccountResponseDTO(account));
    }

    @PostMapping("/deposit")
    public ResponseEntity<AccountResponseDTO> deposit(@Valid @RequestBody DepositDTO body) {
        Account account = accountMovementService.deposit(body);
        return ResponseEntity.ok(new AccountResponseDTO(account));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<AccountResponseDTO> withdraw(@Valid @RequestBody WithdrawDTO body) {
        Account account = accountMovementService.withdraw(body);
        return ResponseEntity.ok(new AccountResponseDTO(account));
    }
}
