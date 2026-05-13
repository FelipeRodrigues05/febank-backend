package com.spring.bank.account.service;

import com.spring.bank.account.dto.DepositDTO;
import com.spring.bank.account.dto.WithdrawDTO;
import com.spring.bank.account.enums.AccountTypeEnum;
import com.spring.bank.account.model.Account;
import com.spring.bank.common.exception.InsufficientFundsException;
import com.spring.bank.common.exception.InvalidAccountException;
import com.spring.bank.account.enums.AccountStatusEnum;
import com.spring.bank.transaction.dto.CreateTransactionDTO;
import com.spring.bank.transaction.enums.TransactionTypeEnum;
import com.spring.bank.transaction.service.CreateTransactionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountMovementService {

    private static final Logger log = LoggerFactory.getLogger(AccountMovementService.class);

    private final FindAccountService findAccountService;
    private final CreateTransactionService createTransactionService;

    @Transactional
    public Account deposit(DepositDTO data) {
        Account checkingAccount = findAccountService.getFirstByUserAndType(data.userId(), AccountTypeEnum.CHECKING);
        Account savingsAccount  = findAccountService.getFirstByUserAndType(data.userId(), AccountTypeEnum.SAVINGS);

        requireActive(checkingAccount, "Checking account is not active.");
        requireActive(savingsAccount, "Savings account is not active.");

        if (checkingAccount.getBalance().compareTo(data.amount()) < 0) {
            throw new InsufficientFundsException("Insufficient balance for deposit.");
        }

        createTransactionService.create(
                new CreateTransactionDTO(checkingAccount, TransactionTypeEnum.DEBIT, data.amount(), "TRANSFER TO SAVINGS")
        );
        createTransactionService.create(
                new CreateTransactionDTO(savingsAccount, TransactionTypeEnum.DEPOSIT, data.amount(), "DEPOSIT TO SAVING ACCOUNT")
        );

        log.info("Deposit queued: userId={} amount={}", data.userId(), data.amount());
        return savingsAccount;
    }

    @Transactional
    public Account withdraw(WithdrawDTO data) {
        Account checkingAccount = findAccountService.getFirstByUserAndType(data.userId(), AccountTypeEnum.CHECKING);
        Account savingsAccount  = findAccountService.getFirstByUserAndType(data.userId(), AccountTypeEnum.SAVINGS);

        requireActive(checkingAccount, "Checking account is not active.");
        requireActive(savingsAccount, "Savings account is not active.");

        if (savingsAccount.getBalance().compareTo(data.amount()) < 0) {
            throw new InsufficientFundsException("Insufficient balance for withdrawal.");
        }

        createTransactionService.create(
                new CreateTransactionDTO(savingsAccount, TransactionTypeEnum.WITHDRAW, data.amount(), "WITHDRAW TO CHECKING ACCOUNT")
        );
        createTransactionService.create(
                new CreateTransactionDTO(checkingAccount, TransactionTypeEnum.CREDIT, data.amount(), "TRANSFER FROM SAVINGS")
        );

        log.info("Withdraw queued: userId={} amount={}", data.userId(), data.amount());
        return checkingAccount;
    }

    private void requireActive(Account account, String message) {
        if (account.getStatus() != AccountStatusEnum.ACTIVE) {
            throw new InvalidAccountException(message);
        }
    }
}
