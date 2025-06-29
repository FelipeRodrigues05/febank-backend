package com.spring.bank.domain.service;

import com.spring.bank.common.exception.AccountNotFoundException;
import com.spring.bank.common.exception.InsufficientFundsException;
import com.spring.bank.common.utils.AccountNumberGenerator;
import com.spring.bank.domain.dto.account.DepositDTO;
import com.spring.bank.domain.dto.account.WithdrawDTO;
import com.spring.bank.domain.dto.transaction.CreateTransactionDTO;
import com.spring.bank.domain.enums.account.AccountStatusEnum;
import com.spring.bank.domain.enums.account.AccountTypeEnum;
import com.spring.bank.domain.enums.transaction.TransactionTypeEnum;
import com.spring.bank.domain.model.Account;
import com.spring.bank.domain.model.User;
import com.spring.bank.domain.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountNumberGenerator accountNumberGenerator;
    private final TransactionService transactionService;

    public Account openAccount(User user) {
        Account account = new Account();
        account.setBalance(BigDecimal.ZERO);
        account.setUser(user);
        account.setType(AccountTypeEnum.CHECKING);
        account.setStatus(AccountStatusEnum.ACTIVE);

        String generatedNumber;

        do {
            generatedNumber = accountNumberGenerator.generateAccount();
        } while (accountRepository.existsByNumber(generatedNumber));

        account.setNumber(generatedNumber);

        return this.accountRepository.save(account);
    }


    public Account getAccountByNumber(String accountNumber) throws AccountNotFoundException {
        return this.accountRepository.findByNumber(accountNumber).orElseThrow(() -> new AccountNotFoundException(
                String.format("Account with NUMBER %s not found", accountNumber)
        ));
    }

    public Account getAccountById(UUID accountId) throws AccountNotFoundException {
        return this.getById(accountId);
    }

    @Transactional
    public Account deposit(DepositDTO data) throws AccountNotFoundException {
        Account account = this.getById(data.accountId());

        account.setBalance(account.getBalance().add(data.amount()));

        this.transactionService.create(
                new CreateTransactionDTO(
                        account,
                        TransactionTypeEnum.DEPOSIT,
                        data.amount(),
                        "DEPOSIT"
                )
        );

        return this.accountRepository.save(account);
    }

    @Transactional
    public Account withdraw(WithdrawDTO data) throws AccountNotFoundException, InsufficientFundsException {
        Account account = this.getById(data.accountId());

        if (account.getBalance().compareTo(data.amount()) < 0) {
            throw new InsufficientFundsException("Insufficient balance for withdrawal.");
        }

        account.setBalance(account.getBalance().subtract(data.amount()));

        this.transactionService.create(
                new CreateTransactionDTO(
                        account,
                        TransactionTypeEnum.WITHDRAW,
                        data.amount(),
                        "WITHDRAW"
                )
        );

        return this.accountRepository.save(account);
    }

    private Account getById(UUID id) throws AccountNotFoundException{
        return this.accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException(
                String.format("Account with ID %s not found", id)
        ));
    }
}
