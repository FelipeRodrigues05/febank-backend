package com.spring.bank.domain.service;

import com.spring.bank.common.exception.InsufficientFundsException;
import com.spring.bank.domain.dto.transaction.CreateTransactionDTO;
import com.spring.bank.domain.dto.transfer.CreateTransferDTO;
import com.spring.bank.domain.enums.transaction.TransactionTypeEnum;
import com.spring.bank.domain.model.Account;
import com.spring.bank.domain.model.Transfer;
import com.spring.bank.domain.repository.TransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransferService {
    private final TransferRepository transferRepository;
    private final TransactionService transactionService;
    private final AccountService accountService;

    public Transfer create(CreateTransferDTO data) throws InsufficientFundsException {
        Account fromAccount = this.accountService.getById(data.fromAccount());
        Account toAccount = this.accountService.getById(data.toAccount());

        this.validateFunds(fromAccount.getBalance(), data.amount());

        Transfer transfer = new Transfer();
        transfer.setFromAccount(fromAccount);
        transfer.setToAccount(toAccount);
        transfer.setAmount(data.amount());
        if(data.description() != null) transfer.setDescription(data.description());

        this.accountService.addFunds(toAccount.getId(), data.amount());
        this.accountService.subtractFunds(fromAccount.getId(), data.amount());

        this.transactionService.create(
                new CreateTransactionDTO(
                        fromAccount,
                        TransactionTypeEnum.DEBIT,
                        data.amount(),
                        data.description()
                )
        );

        this.transactionService.create(
                new CreateTransactionDTO(
                        toAccount,
                        TransactionTypeEnum.CREDIT,
                        data.amount(),
                        data.description()
                )
        );

        return this.transferRepository.save(transfer);
    }

    private void validateFunds(BigDecimal accountBalance, BigDecimal amount) throws InsufficientFundsException {
        if (accountBalance.compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient Funds");
        }
    }
}
