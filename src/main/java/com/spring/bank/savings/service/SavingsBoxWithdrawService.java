package com.spring.bank.savings.service;

import com.spring.bank.account.enums.AccountStatusEnum;
import com.spring.bank.account.model.Account;
import com.spring.bank.account.service.AccountFundsService;
import com.spring.bank.common.exception.InsufficientFundsException;
import com.spring.bank.common.exception.InvalidAccountException;
import com.spring.bank.savings.dto.SavingsBoxWithdrawDTO;
import com.spring.bank.savings.model.SavingsBox;
import com.spring.bank.savings.repository.SavingsBoxRepository;
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
public class SavingsBoxWithdrawService {

    private static final Logger log = LoggerFactory.getLogger(SavingsBoxWithdrawService.class);

    private final SavingsBoxRepository savingsBoxRepository;
    private final AccountFundsService accountFundsService;
    private final CreateTransactionService createTransactionService;
    private final FindSavingsBoxService findSavingsBoxService;

    @Transactional
    public SavingsBox withdraw(Long boxId, SavingsBoxWithdrawDTO data) {
        SavingsBox box = findSavingsBoxService.getById(boxId);
        Account account = box.getAccount();
        requireActive(account);

        if (box.getBalance().compareTo(data.amount()) < 0) {
            throw new InsufficientFundsException("Insufficient balance in savings box.");
        }

        box.setBalance(box.getBalance().subtract(data.amount()));
        accountFundsService.addFunds(account.getId(), data.amount());

        createTransactionService.create(new CreateTransactionDTO(account, TransactionTypeEnum.CREDIT, data.amount(), "SAVINGS BOX WITHDRAW: " + box.getName()));

        SavingsBox saved = savingsBoxRepository.save(box);
        log.info("Savings box withdraw: boxId={} amount={}", boxId, data.amount());
        return saved;
    }

    private void requireActive(Account account) {
        if (account.getStatus() != AccountStatusEnum.ACTIVE) {
            throw new InvalidAccountException("Account is not active.");
        }
    }
}
