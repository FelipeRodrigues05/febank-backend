package com.spring.bank.savings.service;

import com.spring.bank.account.enums.AccountStatusEnum;
import com.spring.bank.account.model.Account;
import com.spring.bank.account.service.AccountFundsService;
import com.spring.bank.account.service.FindAccountService;
import com.spring.bank.common.exception.InvalidAccountException;
import com.spring.bank.savings.dto.CreateSavingsBoxDTO;
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

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CreateSavingsBoxService {

    private static final Logger log = LoggerFactory.getLogger(CreateSavingsBoxService.class);

    private final SavingsBoxRepository savingsBoxRepository;
    private final FindAccountService findAccountService;
    private final AccountFundsService accountFundsService;
    private final CreateTransactionService createTransactionService;

    @Transactional
    public SavingsBox create(CreateSavingsBoxDTO data) {
        Account account = findAccountService.getById(data.accountId());
        requireActive(account);

        SavingsBox box = new SavingsBox();
        box.setAccount(account);
        box.setName(data.name());
        box.setImageUrl(data.imageUrl());
        box.setBalance(BigDecimal.ZERO);

        if (data.initialAmount() != null) {
            accountFundsService.subtractFunds(account.getId(), data.initialAmount());
            createTransactionService.create(new CreateTransactionDTO(account, TransactionTypeEnum.DEBIT, data.initialAmount(), "SAVINGS BOX: " + data.name()));
            box.setBalance(data.initialAmount());
        }

        SavingsBox saved = savingsBoxRepository.save(box);
        log.info("Savings box created: accountId={} name='{}' id={}", data.accountId(), data.name(), saved.getId());
        return saved;
    }

    private void requireActive(Account account) {
        if (account.getStatus() != AccountStatusEnum.ACTIVE) {
            throw new InvalidAccountException("Account is not active.");
        }
    }
}
