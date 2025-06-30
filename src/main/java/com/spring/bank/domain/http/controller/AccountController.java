package com.spring.bank.domain.http.controller;

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
    public ResponseEntity<String> createAccount(@Validated @RequestBody OpenAccountDTO body)
    {
        this.accountService.openAccount(body);

        return ResponseEntity.status(HttpStatus.CREATED).body("Account created successfully");
    }

    @GetMapping("/{number}")
    public ResponseEntity<Account> getAccount(@RequestParam String number)
    {
        Account account = this.accountService.getAccountByNumber(number);

        return ResponseEntity.status(HttpStatus.OK).body(account);
    }

    @PostMapping("/deposit")
    public ResponseEntity<String> deposit(@Validated @RequestBody DepositDTO body)
    {
        this.accountService.deposit(body);

        return ResponseEntity.status(HttpStatus.OK).body("Successfully deposited value");
    }

    @PostMapping("/withdraw")
    public ResponseEntity<String> withdraw(@Validated @RequestBody WithdrawDTO body)
    {
        this.accountService.withdraw(body);

        return ResponseEntity.status(HttpStatus.OK).body("Successfully withdrawed value");
    }
}
