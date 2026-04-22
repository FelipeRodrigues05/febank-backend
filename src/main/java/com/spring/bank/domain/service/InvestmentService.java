package com.spring.bank.domain.service;

import com.spring.bank.common.utils.IncomeCalculator;
import com.spring.bank.domain.dto.investment.ApplyInvestmentDTO;
import com.spring.bank.domain.dto.investment.RedeemInvestmentDTO;
import com.spring.bank.domain.dto.transaction.CreateTransactionDTO;
import com.spring.bank.domain.enums.account.AccountStatusEnum;
import com.spring.bank.domain.enums.account.AccountTypeEnum;
import com.spring.bank.domain.enums.transaction.TransactionTypeEnum;
import com.spring.bank.domain.model.Account;
import com.spring.bank.domain.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.spring.bank.common.exception.InsufficientFundsException;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvestmentService {

    private static final Logger log = LoggerFactory.getLogger(InvestmentService.class);

    private final AccountRepository accountRepository;
    private final TransactionService transactionService;
    private final IncomeCalculator incomeCalculator;
    private final AccountService accountService;

    @Transactional
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

        Account saved = this.accountRepository.save(investmentAccount);
        log.info("Investment applied: userId={} amount={}", data.userId(), data.amount());
        return saved;
    }

    @Transactional
    public Account redeem(RedeemInvestmentDTO data) {
        Account investmentAccount = this.accountService.getFirstByUser(data.userId(), AccountTypeEnum.INVESTMENT);
        Account checkingAccount   = this.accountService.getFirstByUser(data.userId(), AccountTypeEnum.CHECKING);

        if (investmentAccount.getBalance().compareTo(data.amount()) < 0) {
            throw new InsufficientFundsException("Insufficient balance to redeem investment.");
        }

        this.accountService.subtractFunds(investmentAccount.getId(), data.amount());
        this.accountService.addFunds(checkingAccount.getId(), data.amount());

        this.createTransaction(investmentAccount, TransactionTypeEnum.DEBIT, data.amount(), "INVESTMENT REDEMPTION");
        this.createTransaction(checkingAccount, TransactionTypeEnum.CREDIT, data.amount(), "FROM INVESTMENT ACCOUNT");

        log.info("Investment redeemed: userId={} amount={}", data.userId(), data.amount());
        return this.accountService.getById(investmentAccount.getId());
    }

    @Transactional
    public void processDailyEarnings() {
        List<Account> accounts = this.accountRepository.findAllByTypeAndStatus(AccountTypeEnum.INVESTMENT, AccountStatusEnum.ACTIVE);

        if (accounts.isEmpty()) {
            log.info("processDailyEarnings: no active investment accounts found, skipping");
            return;
        }

        double dailyRate = incomeCalculator.daily();

        for (Account account : accounts) {
            if (account.getBalance().compareTo(BigDecimal.ZERO) <= 0) continue;

            BigDecimal earnings = account.getBalance().multiply(BigDecimal.valueOf(dailyRate));
            BigDecimal updatedBalance = account.getBalance().add(earnings);

            this.createTransaction(account, TransactionTypeEnum.FEE, earnings, "INVESTMENT EARNING");
            account.setBalance(updatedBalance);
        }

        this.accountRepository.saveAll(accounts);
        log.info("Daily earnings processed for {} investment accounts", accounts.size());
    }

    private void createTransaction(Account account, TransactionTypeEnum type, BigDecimal amount, String description) {
        this.transactionService.create(
                new CreateTransactionDTO(account, type, amount, description)
        );
    }
}
