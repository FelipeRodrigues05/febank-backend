package com.spring.bank.domain.service;

import com.spring.bank.common.config.messaging.RabbitMQConfig;
import com.spring.bank.common.exception.InsufficientFundsException;
import com.spring.bank.common.exception.InvalidAccountException;
import com.spring.bank.common.exception.InvalidTransferAccountTypeException;
import com.spring.bank.common.exception.TransferNotFoundException;
import com.spring.bank.domain.dto.transfer.CreateTransferDTO;
import com.spring.bank.domain.enums.account.AccountStatusEnum;
import com.spring.bank.domain.enums.account.AccountTypeEnum;
import com.spring.bank.domain.enums.transfer.TransferStatusEnum;
import com.spring.bank.domain.model.Account;
import com.spring.bank.domain.model.Transfer;
import com.spring.bank.domain.repository.TransferRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransferService {

    private static final Logger log = LoggerFactory.getLogger(TransferService.class);

    private final TransferRepository transferRepository;
    private final AccountService accountService;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public Transfer create(CreateTransferDTO data) {
        Account fromAccount = this.accountService.getById(data.fromAccount());
        Account toAccount   = this.accountService.getById(data.toAccount());

        if (fromAccount.getId().equals(toAccount.getId())) {
            throw new InvalidTransferAccountTypeException("Cannot transfer to the same account.");
        }

        if (fromAccount.getType() != AccountTypeEnum.CHECKING || toAccount.getType() != AccountTypeEnum.CHECKING) {
            throw new InvalidTransferAccountTypeException("You can only transfer if both accounts are CHECKING");
        }

        if (fromAccount.getStatus() != AccountStatusEnum.ACTIVE) {
            throw new InvalidAccountException("Source account is not active.");
        }

        if (toAccount.getStatus() != AccountStatusEnum.ACTIVE) {
            throw new InvalidAccountException("Destination account is not active.");
        }

        if (fromAccount.getBalance().compareTo(data.amount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds for transfer");
        }

        Transfer transfer = new Transfer();
        transfer.setFromAccount(fromAccount);
        transfer.setToAccount(toAccount);
        transfer.setAmount(data.amount());
        transfer.setStatus(TransferStatusEnum.PENDING);
        if (data.description() != null) transfer.setDescription(data.description());

        Transfer saved = this.transferRepository.save(transfer);
        this.rabbitTemplate.convertAndSend(RabbitMQConfig.TRANSFER_QUEUE, saved.getId());

        log.info("Transfer queued: id={} from={} to={} amount={}", saved.getId(), fromAccount.getId(), toAccount.getId(), data.amount());
        return saved;
    }

    @Transactional
    public void completeTransfer(Long transferId) {
        Transfer transfer = this.transferRepository.findById(transferId).orElseThrow(() ->
                new TransferNotFoundException(String.format("Transfer with ID %s not found", transferId))
        );

        transfer.setStatus(TransferStatusEnum.COMPLETED);
        this.transferRepository.save(transfer);
        log.info("Transfer completed: id={}", transferId);
    }
}
