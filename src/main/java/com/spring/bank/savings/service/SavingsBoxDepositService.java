package com.spring.bank.savings.service;

import com.spring.bank.account.enums.AccountStatusEnum;
import com.spring.bank.account.model.Account;
import com.spring.bank.account.service.AccountFundsService;
import com.spring.bank.common.exception.InvalidAccountException;
import com.spring.bank.savings.dto.SavingsBoxDepositDTO;
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
public class SavingsBoxDepositService {

    private static final Logger log = LoggerFactory.getLogger(SavingsBoxDepositService.class);

    private final SavingsBoxRepository savingsBoxRepository;
    private final AccountFundsService accountFundsService;
    private final CreateTransactionService createTransactionService;
    private final FindSavingsBoxService findSavingsBoxService;

    @Transactional
    public SavingsBox deposit(Long boxId, SavingsBoxDepositDTO data) {
        SavingsBox box = findSavingsBoxService.getById(boxId);
        Account account = box.getAccount();
        requireActive(account);

        accountFundsService.subtractFunds(account.getId(), data.amount());
        box.setBalance(box.getBalance().add(data.amount()));

        createTransactionService.create(new CreateTransactionDTO(account, TransactionTypeEnum.DEBIT, data.amount(), "SAVINGS BOX DEPOSIT: " + box.getName()));

        SavingsBox saved = savingsBoxRepository.save(box);
        log.info("Savings box deposit: boxId={} amount={}", boxId, data.amount());
        return saved;
    }

    private void requireActive(Account account) {
        if (account.getStatus() != AccountStatusEnum.ACTIVE) {
            throw new InvalidAccountException("Account is not active.");
        }
    }
}
