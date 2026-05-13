package com.spring.bank.investment.service;

import com.spring.bank.account.enums.AccountStatusEnum;
import com.spring.bank.account.enums.AccountTypeEnum;
import com.spring.bank.account.model.Account;
import com.spring.bank.account.repository.AccountRepository;
import com.spring.bank.common.utils.IncomeCalculator;
import com.spring.bank.transaction.dto.CreateTransactionDTO;
import com.spring.bank.transaction.enums.TransactionTypeEnum;
import com.spring.bank.transaction.service.CreateTransactionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyEarningsService {

    private static final Logger log = LoggerFactory.getLogger(DailyEarningsService.class);

    private final AccountRepository accountRepository;
    private final CreateTransactionService createTransactionService;
    private final IncomeCalculator incomeCalculator;

    @Transactional
    public void processEarnings() {
        List<Account> accounts = accountRepository.findAllByTypeAndStatus(AccountTypeEnum.INVESTMENT, AccountStatusEnum.ACTIVE);

        if (accounts.isEmpty()) {
            log.info("processEarnings: no active investment accounts found, skipping");
            return;
        }

        double dailyRate = incomeCalculator.daily();

        for (Account account : accounts) {
            if (account.getBalance().compareTo(BigDecimal.ZERO) <= 0) continue;

            BigDecimal earnings = account.getBalance().multiply(BigDecimal.valueOf(dailyRate));
            BigDecimal updatedBalance = account.getBalance().add(earnings);

            createTransactionService.create(new CreateTransactionDTO(account, TransactionTypeEnum.FEE, earnings, "INVESTMENT EARNING"));
            account.setBalance(updatedBalance);
        }

        accountRepository.saveAll(accounts);
        log.info("Daily earnings processed for {} investment accounts", accounts.size());
    }
}
