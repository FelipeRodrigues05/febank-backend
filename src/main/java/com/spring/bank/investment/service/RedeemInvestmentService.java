package com.spring.bank.investment.service;

import com.spring.bank.account.enums.AccountTypeEnum;
import com.spring.bank.account.model.Account;
import com.spring.bank.account.service.AccountFundsService;
import com.spring.bank.account.service.FindAccountService;
import com.spring.bank.common.exception.InsufficientFundsException;
import com.spring.bank.investment.dto.RedeemInvestmentDTO;
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
public class RedeemInvestmentService {

    private static final Logger log = LoggerFactory.getLogger(RedeemInvestmentService.class);

    private final FindAccountService findAccountService;
    private final AccountFundsService accountFundsService;
    private final CreateTransactionService createTransactionService;

    @Transactional
    public Account redeem(RedeemInvestmentDTO data) {
        Account investmentAccount = findAccountService.getFirstByUserAndType(data.userId(), AccountTypeEnum.INVESTMENT);
        Account checkingAccount   = findAccountService.getFirstByUserAndType(data.userId(), AccountTypeEnum.CHECKING);

        if (investmentAccount.getBalance().compareTo(data.amount()) < 0) {
            throw new InsufficientFundsException("Insufficient balance to redeem investment.");
        }

        accountFundsService.subtractFunds(investmentAccount.getId(), data.amount());
        accountFundsService.addFunds(checkingAccount.getId(), data.amount());

        createTransactionService.create(new CreateTransactionDTO(investmentAccount, TransactionTypeEnum.DEBIT, data.amount(), "INVESTMENT REDEMPTION"));
        createTransactionService.create(new CreateTransactionDTO(checkingAccount, TransactionTypeEnum.CREDIT, data.amount(), "FROM INVESTMENT ACCOUNT"));

        log.info("Investment redeemed: userId={} amount={}", data.userId(), data.amount());
        return findAccountService.getById(investmentAccount.getId());
    }
}
