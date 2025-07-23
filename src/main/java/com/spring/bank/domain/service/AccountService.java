package com.spring.bank.domain.service;

import com.spring.bank.common.exception.AccountNotFoundException;
import com.spring.bank.common.exception.InsufficientFundsException;
import com.spring.bank.common.exception.InvalidTransferAccountTypeException;
import com.spring.bank.common.utils.AccountNumberGenerator;
import com.spring.bank.domain.dto.account.DepositDTO;
import com.spring.bank.domain.dto.account.OpenAccountDTO;
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
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountNumberGenerator accountNumberGenerator;
    private final TransactionService transactionService;
    private final UserService userService;

    public Account openAccount(OpenAccountDTO data) {
        User user = this.userService.getById(data.userId());

        Account account = new Account();
        account.setBalance(BigDecimal.ZERO);
        account.setUser(user);
        account.setType(data.type());
        account.setStatus(AccountStatusEnum.ACTIVE);

        String generatedNumber;

        do {
            generatedNumber = accountNumberGenerator.generateAccount();
        } while (accountRepository.existsByNumber(generatedNumber));

        account.setNumber(generatedNumber);

        return this.accountRepository.save(account);
    }

    @Transactional
    public Account deposit(DepositDTO data) throws AccountNotFoundException {
        Account checkingAccount = this.getFirstByUser(data.userId(), AccountTypeEnum.CHECKING);
        Account savingsAccount = this.getFirstByUser(data.userId(), AccountTypeEnum.SAVINGS);

        if (checkingAccount.getBalance().compareTo(data.amount()) < 0) {
            throw new InsufficientFundsException("Insufficient balance for deposit.");
        }

        checkingAccount.setBalance(checkingAccount.getBalance().subtract(data.amount()));
        savingsAccount.setBalance(savingsAccount.getBalance().add(data.amount()));

        this.transactionService.create(
                new CreateTransactionDTO(savingsAccount, TransactionTypeEnum.DEPOSIT, data.amount(), "DEPOSIT TO SAVING ACCOUNT")
        );

        this.accountRepository.save(checkingAccount);
        return this.accountRepository.save(savingsAccount);
    }

    @Transactional
    public Account withdraw(WithdrawDTO data) throws AccountNotFoundException, InsufficientFundsException {
        Account checkingAccount = this.getFirstByUser(data.userId(), AccountTypeEnum.CHECKING);
        Account savingsAccount = this.getFirstByUser(data.userId(), AccountTypeEnum.SAVINGS);

        if (savingsAccount.getBalance().compareTo(data.amount()) < 0) {
            throw new InsufficientFundsException("Insufficient balance for withdrawal.");
        }

        checkingAccount.setBalance(checkingAccount.getBalance().add(data.amount()));
        savingsAccount.setBalance(savingsAccount.getBalance().subtract(data.amount()));

        this.transactionService.create(
                new CreateTransactionDTO(savingsAccount, TransactionTypeEnum.WITHDRAW, data.amount(),"WITHDRAW TO CHECKING ACCOUNT")
        );

        this.accountRepository.save(checkingAccount);
        return this.accountRepository.save(savingsAccount);
    }

    public void addFunds(Long accountId, BigDecimal amount) {
        Account account = this.getById(accountId);

        account.setBalance(account.getBalance().add(amount));

        this.accountRepository.save(account);
    }

    public void subtractFunds(Long accountId, BigDecimal amount) {
        Account account = this.getById(accountId);

        account.setBalance(account.getBalance().subtract(amount));

        this.accountRepository.save(account);
    }

    public Account getAccountByNumber(String accountNumber) throws AccountNotFoundException {
        return this.accountRepository.findByNumber(accountNumber).orElseThrow(() -> new AccountNotFoundException(
                String.format("Account with NUMBER %s not found", accountNumber)
        ));
    }

    public Account getById(Long id) throws AccountNotFoundException {
        return this.accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException(
                String.format("Account with ID %s not found", id)
        ));
    }

    public Account getFirstByUser(Long userId, AccountTypeEnum type) {
        return this.accountRepository.findFirstByUserIdAndType(userId, type).orElseThrow(() -> new RuntimeException("No accounts found for this user"));
    }
}
