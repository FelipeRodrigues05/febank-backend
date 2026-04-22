package com.spring.bank.domain.service;

import com.spring.bank.common.exception.InsufficientFundsException;
import com.spring.bank.common.exception.InvalidAccountException;
import com.spring.bank.common.exception.SavingsBoxNotFoundException;
import com.spring.bank.domain.dto.savingsbox.CreateSavingsBoxDTO;
import com.spring.bank.domain.dto.savingsbox.SavingsBoxDepositDTO;
import com.spring.bank.domain.dto.savingsbox.SavingsBoxWithdrawDTO;
import com.spring.bank.domain.dto.transaction.CreateTransactionDTO;
import com.spring.bank.domain.enums.account.AccountStatusEnum;
import com.spring.bank.domain.enums.transaction.TransactionTypeEnum;
import com.spring.bank.domain.model.Account;
import com.spring.bank.domain.model.SavingsBox;
import com.spring.bank.domain.repository.SavingsBoxRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SavingsBoxService {

    private static final Logger log = LoggerFactory.getLogger(SavingsBoxService.class);

    private final SavingsBoxRepository savingsBoxRepository;
    private final AccountService accountService;
    private final TransactionService transactionService;

    @Transactional
    public SavingsBox create(CreateSavingsBoxDTO data) {
        Account account = accountService.getById(data.accountId());
        requireActive(account);

        SavingsBox box = new SavingsBox();
        box.setAccount(account);
        box.setName(data.name());
        box.setImageUrl(data.imageUrl());
        box.setBalance(BigDecimal.ZERO);

        if (data.initialAmount() != null) {
            accountService.subtractFunds(account.getId(), data.initialAmount());
            transactionService.create(new CreateTransactionDTO(account, TransactionTypeEnum.DEBIT, data.initialAmount(), "SAVINGS BOX: " + data.name()));
            box.setBalance(data.initialAmount());
        }

        SavingsBox saved = savingsBoxRepository.save(box);
        log.info("Savings box created: accountId={} name='{}' id={}", data.accountId(), data.name(), saved.getId());
        return saved;
    }

    @Transactional
    public SavingsBox deposit(Long boxId, SavingsBoxDepositDTO data) {
        SavingsBox box = getById(boxId);
        Account account = box.getAccount();
        requireActive(account);

        accountService.subtractFunds(account.getId(), data.amount());
        box.setBalance(box.getBalance().add(data.amount()));

        transactionService.create(new CreateTransactionDTO(account, TransactionTypeEnum.DEBIT, data.amount(), "SAVINGS BOX DEPOSIT: " + box.getName()));

        SavingsBox saved = savingsBoxRepository.save(box);
        log.info("Savings box deposit: boxId={} amount={}", boxId, data.amount());
        return saved;
    }

    @Transactional
    public SavingsBox withdraw(Long boxId, SavingsBoxWithdrawDTO data) {
        SavingsBox box = getById(boxId);
        Account account = box.getAccount();
        requireActive(account);

        if (box.getBalance().compareTo(data.amount()) < 0) {
            throw new InsufficientFundsException("Insufficient balance in savings box.");
        }

        box.setBalance(box.getBalance().subtract(data.amount()));
        accountService.addFunds(account.getId(), data.amount());

        transactionService.create(new CreateTransactionDTO(account, TransactionTypeEnum.CREDIT, data.amount(), "SAVINGS BOX WITHDRAW: " + box.getName()));

        SavingsBox saved = savingsBoxRepository.save(box);
        log.info("Savings box withdraw: boxId={} amount={}", boxId, data.amount());
        return saved;
    }

    public List<SavingsBox> listByAccount(Long accountId) {
        return savingsBoxRepository.findAllByAccountId(accountId);
    }

    public SavingsBox getById(Long id) {
        return savingsBoxRepository.findById(id).orElseThrow(() ->
                new SavingsBoxNotFoundException(String.format("Savings box with ID %s not found", id))
        );
    }

    private void requireActive(Account account) {
        if (account.getStatus() != AccountStatusEnum.ACTIVE) {
            throw new InvalidAccountException("Account is not active.");
        }
    }
}
