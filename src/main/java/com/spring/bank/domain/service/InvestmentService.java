package com.spring.bank.domain.service;

import com.spring.bank.common.exception.AccountTypeNotFoundException;
import com.spring.bank.common.exception.InsufficientFundsException;
import com.spring.bank.common.utils.IncomeCalculator;
import com.spring.bank.domain.dto.investment.ApplyInvestmentDTO;
import com.spring.bank.domain.dto.transaction.CreateTransactionDTO;
import com.spring.bank.domain.enums.account.AccountTypeEnum;
import com.spring.bank.domain.enums.transaction.TransactionTypeEnum;
import com.spring.bank.domain.model.Account;
import com.spring.bank.domain.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InvestmentService {

    private final AccountRepository accountRepository;
    private final TransactionService transactionService;
    private final IncomeCalculator incomeCalculator;
    private final AccountService accountService;

    public Account apply(ApplyInvestmentDTO data) {
        Account checkingAccount   = this.accountService.getFirstByUser(data.userId(), AccountTypeEnum.CHECKING);
        Account investmentAccount = this.accountService.getFirstByUser(data.userId(), AccountTypeEnum.INVESTMENT);

        if (checkingAccount.getBalance().compareTo(data.amount()) < 0) {
            throw new InsufficientFundsException("Insufficient balance to apply investment.");
        }

        this.accountService.subtractFunds(checkingAccount.getId(), data.amount());
        this.accountService.addFunds(investmentAccount.getId(), data.amount());

        this.createTransaction(checkingAccount, TransactionTypeEnum.DEBIT, data.amount(), "TO INVESTMENT ACCOUNT");
        this.createTransaction(investmentAccount, TransactionTypeEnum.CREDIT, data.amount(), "FROM CHECKING ACCOUNT");

        return this.accountRepository.save(investmentAccount);
    }

    public void processDailyEarnings() {
        List<Account> accounts = this.accountRepository.findAllByType(AccountTypeEnum.INVESTMENT).orElseThrow(() -> new AccountTypeNotFoundException("INVESTMENT accounts not found for any users"));
        double dailyRate = incomeCalculator.daily();

        for (Account account : accounts) {
            BigDecimal updatedAmount = account.getBalance().multiply(BigDecimal.valueOf((1 + dailyRate)));
            this.createTransaction(account, TransactionTypeEnum.FEE, updatedAmount.subtract(account.getBalance()), "INVESTMENT EARNING");

            account.setBalance(updatedAmount);
        }

        this.accountRepository.saveAll(accounts);
    }

    private void createTransaction(Account account, TransactionTypeEnum type, BigDecimal amount, String description) {
        this.transactionService.create(
                new CreateTransactionDTO(account, type, amount, description)
        );
    }
}
