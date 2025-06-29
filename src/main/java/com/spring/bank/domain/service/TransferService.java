package com.spring.bank.domain.service;

import com.spring.bank.common.exception.InsufficientFundsException;
import com.spring.bank.domain.dto.transaction.CreateTransactionDTO;
import com.spring.bank.domain.dto.transfer.CreateTransferDTO;
import com.spring.bank.domain.enums.transaction.TransactionTypeEnum;
import com.spring.bank.domain.model.Transfer;
import com.spring.bank.domain.repository.TransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransferService {
    private TransferRepository transferRepository;
    private TransactionService transactionService;

    public Transfer create(CreateTransferDTO data) throws InsufficientFundsException {
        this.validateFunds(data.fromAccount().getBalance(), data.amount());

        Transfer transfer = new Transfer();
        transfer.setFromAccount(data.fromAccount());
        transfer.setToAccount(data.toAccount());

        this.transactionService.create(
                new CreateTransactionDTO(
                        data.fromAccount(),
                        TransactionTypeEnum.DEBIT,
                        data.amount(),
                        data.description()
                )
        );

        this.transactionService.create(
                new CreateTransactionDTO(
                        data.toAccount(),
                        TransactionTypeEnum.CREDIT,
                        data.amount(),
                        data.description()
                )
        );

        return this.transferRepository.save(transfer);
    }

    private void validateFunds(BigDecimal accountBalance, BigDecimal amount) throws InsufficientFundsException {
        if(accountBalance.compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient Funds");
        }
    }
}
