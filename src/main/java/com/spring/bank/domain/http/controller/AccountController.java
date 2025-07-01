package com.spring.bank.domain.http.controller;

import com.spring.bank.domain.dto.account.AccountResponseDTO;
import com.spring.bank.domain.dto.account.DepositDTO;
import com.spring.bank.domain.dto.account.OpenAccountDTO;
import com.spring.bank.domain.dto.account.WithdrawDTO;
import com.spring.bank.domain.model.Account;
import com.spring.bank.domain.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(name = "account", path = "/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/open")
    public ResponseEntity<AccountResponseDTO> createAccount(@Validated @RequestBody OpenAccountDTO body) {
        Account account = this.accountService.openAccount(body);

        return ResponseEntity.status(HttpStatus.CREATED).body(new AccountResponseDTO(account));
    }

    @GetMapping("/{number}")
    public ResponseEntity<AccountResponseDTO> getAccount(@RequestParam String number) {
        Account account = this.accountService.getAccountByNumber(number);

        return ResponseEntity.status(HttpStatus.OK).body(new AccountResponseDTO(account));
    }

    @PostMapping("/deposit")
    public ResponseEntity<AccountResponseDTO> deposit(@Validated @RequestBody DepositDTO body) {
        Account account = this.accountService.deposit(body);

        return ResponseEntity.status(HttpStatus.OK).body(new AccountResponseDTO(account));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<AccountResponseDTO> withdraw(@Validated @RequestBody WithdrawDTO body) {
        Account account = this.accountService.withdraw(body);

        return ResponseEntity.status(HttpStatus.OK).body(new AccountResponseDTO(account));
    }
}
