package com.spring.bank.transfer.service;

import com.spring.bank.account.enums.AccountStatusEnum;
import com.spring.bank.account.enums.AccountTypeEnum;
import com.spring.bank.account.model.Account;
import com.spring.bank.account.service.FindAccountService;
import com.spring.bank.common.exception.InsufficientFundsException;
import com.spring.bank.common.exception.InvalidAccountException;
import com.spring.bank.common.exception.InvalidTransferAccountTypeException;
import com.spring.bank.transfer.dto.CreateTransferDTO;
import com.spring.bank.transfer.enums.TransferStatusEnum;
import com.spring.bank.transfer.messaging.TransferPublisher;
import com.spring.bank.transfer.model.Transfer;
import com.spring.bank.transfer.repository.TransferRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateTransferService {

    private static final Logger log = LoggerFactory.getLogger(CreateTransferService.class);

    private final TransferRepository transferRepository;
    private final FindAccountService findAccountService;
    private final TransferPublisher transferPublisher;

    @Transactional
    public Transfer create(CreateTransferDTO data) {
        Account fromAccount = findAccountService.getById(data.fromAccount());
        Account toAccount   = findAccountService.getById(data.toAccount());

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

        Transfer saved = transferRepository.save(transfer);
        transferPublisher.publish(saved.getId());

        log.info("Transfer queued: id={} from={} to={} amount={}", saved.getId(), fromAccount.getId(), toAccount.getId(), data.amount());
        return saved;
    }
}
