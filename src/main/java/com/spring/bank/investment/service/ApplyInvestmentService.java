package com.spring.bank.investment.service;

import com.spring.bank.account.enums.AccountTypeEnum;
import com.spring.bank.account.model.Account;
import com.spring.bank.account.service.AccountFundsService;
import com.spring.bank.account.service.FindAccountService;
import com.spring.bank.common.exception.InsufficientFundsException;
import com.spring.bank.investment.dto.ApplyInvestmentDTO;
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
public class ApplyInvestmentService {

    private static final Logger log = LoggerFactory.getLogger(ApplyInvestmentService.class);

    private final FindAccountService findAccountService;
    private final AccountFundsService accountFundsService;
    private final CreateTransactionService createTransactionService;

    @Transactional
    public Account apply(ApplyInvestmentDTO data) {
        Account checkingAccount   = findAccountService.getFirstByUserAndType(data.userId(), AccountTypeEnum.CHECKING);
        Account investmentAccount = findAccountService.getFirstByUserAndType(data.userId(), AccountTypeEnum.INVESTMENT);

        if (checkingAccount.getBalance().compareTo(data.amount()) < 0) {
            throw new InsufficientFundsException("Insufficient balance to apply investment.");
        }

        accountFundsService.subtractFunds(checkingAccount.getId(), data.amount());
        accountFundsService.addFunds(investmentAccount.getId(), data.amount());

        createTransactionService.create(new CreateTransactionDTO(checkingAccount, TransactionTypeEnum.DEBIT, data.amount(), "TO INVESTMENT ACCOUNT"));
        createTransactionService.create(new CreateTransactionDTO(investmentAccount, TransactionTypeEnum.CREDIT, data.amount(), "FROM CHECKING ACCOUNT"));

        log.info("Investment applied: userId={} amount={}", data.userId(), data.amount());
        return findAccountService.getById(investmentAccount.getId());
    }
}
