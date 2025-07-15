package com.spring.bank.domain.service;

import com.spring.bank.common.config.messaging.RabbitMQConfig;
import com.spring.bank.common.exception.InsufficientFundsException;
import com.spring.bank.common.exception.InvalidTransferAccountTypeException;
import com.spring.bank.domain.dto.transaction.CreateTransactionDTO;
import com.spring.bank.domain.dto.transfer.CreateTransferDTO;
import com.spring.bank.domain.enums.account.AccountTypeEnum;
import com.spring.bank.domain.enums.transaction.TransactionTypeEnum;
import com.spring.bank.domain.enums.transfer.TransferStatusEnum;
import com.spring.bank.domain.model.Account;
import com.spring.bank.domain.model.Transfer;
import com.spring.bank.domain.repository.TransferRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransferService {
    private final TransferRepository transferRepository;
    private final TransactionService transactionService;
    private final AccountService accountService;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public Transfer create(CreateTransferDTO data) throws InsufficientFundsException {
        Account fromAccount = this.accountService.getById(data.fromAccount());
        Account toAccount = this.accountService.getById(data.toAccount());

        if(fromAccount.getType() != AccountTypeEnum.CHECKING && toAccount.getType() != AccountTypeEnum.CHECKING) {
            throw new InvalidTransferAccountTypeException("You can only transfer if the 2 accounts are CHECKING");
        }

        this.validateFunds(fromAccount.getBalance(), data.amount());

        Transfer transfer = new Transfer();
        transfer.setFromAccount(fromAccount);
        transfer.setToAccount(toAccount);
        transfer.setAmount(data.amount());
        transfer.setStatus(TransferStatusEnum.PENDING);

        if(data.description() != null) transfer.setDescription(data.description());

        this.accountService.addFunds(toAccount.getId(), data.amount());
        this.accountService.subtractFunds(fromAccount.getId(), data.amount());

        this.createTransaction(fromAccount, TransactionTypeEnum.DEBIT, data.amount(), data.description());
        this.createTransaction(toAccount, TransactionTypeEnum.CREDIT, data.amount(), data.description());

        this.transferRepository.save(transfer);

        this.rabbitTemplate.convertAndSend(RabbitMQConfig.TRANSFER_RESPONSE, transfer.getId());

        return transfer;
    }

    @Transactional
    public void completeTransfer(Long transferId) throws Exception {
        Transfer transfer = this.transferRepository.findById(transferId).orElseThrow(() -> new Exception("Transfer not found"));

        transfer.setStatus(TransferStatusEnum.COMPLETED);
        this.transferRepository.save(transfer);
    }

    private void validateFunds(BigDecimal accountBalance, BigDecimal amount) throws InsufficientFundsException {
        if (accountBalance.compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient Funds");
        }
    }

    private void createTransaction(Account account, TransactionTypeEnum type, BigDecimal amount, String description) {
        this.transactionService.create(
                new CreateTransactionDTO(account, type, amount, description)
        );
    }

}
