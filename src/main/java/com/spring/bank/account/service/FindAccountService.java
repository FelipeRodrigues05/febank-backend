package com.spring.bank.account.service;

import com.spring.bank.account.enums.AccountTypeEnum;
import com.spring.bank.account.model.Account;
import com.spring.bank.account.repository.AccountRepository;
import com.spring.bank.common.exception.AccountNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindAccountService {

    private final AccountRepository accountRepository;

    public Account getById(Long id) {
        return accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException(
                String.format("Account with ID %s not found", id)
        ));
    }

    public Account getByNumber(String number) {
        return accountRepository.findByNumber(number).orElseThrow(() -> new AccountNotFoundException(
                String.format("Account with NUMBER %s not found", number)
        ));
    }

    public Account getFirstByUserAndType(Long userId, AccountTypeEnum type) {
        return accountRepository.findFirstByUserIdAndType(userId, type).orElseThrow(() -> new AccountNotFoundException(
                String.format("No %s account found for user %s", type, userId)
        ));
    }
}
