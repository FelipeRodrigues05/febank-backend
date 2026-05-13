package com.spring.bank.pix.service;

import com.spring.bank.account.enums.AccountStatusEnum;
import com.spring.bank.account.enums.AccountTypeEnum;
import com.spring.bank.account.model.Account;
import com.spring.bank.account.service.AccountFundsService;
import com.spring.bank.account.service.FindAccountService;
import com.spring.bank.common.exception.InsufficientFundsException;
import com.spring.bank.common.exception.InvalidAccountException;
import com.spring.bank.common.exception.InvalidTransferAccountTypeException;
import com.spring.bank.pix.dto.PixTransferDTO;
import com.spring.bank.pix.model.PixKey;
import com.spring.bank.pix.repository.PixKeyRepository;
import com.spring.bank.transaction.dto.CreateTransactionDTO;
import com.spring.bank.transaction.enums.TransactionTypeEnum;
import com.spring.bank.transaction.service.CreateTransactionService;
import com.spring.bank.transfer.enums.TransferStatusEnum;
import com.spring.bank.transfer.model.Transfer;
import com.spring.bank.transfer.repository.TransferRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PixTransferService {

    private static final Logger log = LoggerFactory.getLogger(PixTransferService.class);

    private final PixKeyRepository pixKeyRepository;
    private final FindAccountService findAccountService;
    private final AccountFundsService accountFundsService;
    private final CreateTransactionService createTransactionService;
    private final TransferRepository transferRepository;
    private final IncrementPixContactService incrementPixContactService;

    @Transactional
    public Transfer transfer(PixTransferDTO data) {
        Account fromAccount = findAccountService.getById(data.fromAccountId());
        PixKey pixKey = pixKeyRepository.findByKey(data.pixKey())
                .orElseThrow(() -> new com.spring.bank.common.exception.PixKeyNotFoundException("PIX key not found: " + data.pixKey()));
        Account toAccount = pixKey.getAccount();

        if (fromAccount.getId().equals(toAccount.getId())) {
            throw new InvalidTransferAccountTypeException("Cannot PIX to yourself.");
        }
        if (fromAccount.getType() != AccountTypeEnum.CHECKING) {
            throw new InvalidAccountException("PIX must be sent from a CHECKING account.");
        }
        if (fromAccount.getStatus() != AccountStatusEnum.ACTIVE) {
            throw new InvalidAccountException("Source account is not active.");
        }
        if (toAccount.getStatus() != AccountStatusEnum.ACTIVE) {
            throw new InvalidAccountException("Destination account is not active.");
        }
        if (fromAccount.getBalance().compareTo(data.amount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds for PIX transfer.");
        }

        accountFundsService.subtractFunds(fromAccount.getId(), data.amount());
        accountFundsService.addFunds(toAccount.getId(), data.amount());

        String desc = data.description() != null ? data.description() : "PIX: " + data.pixKey();
        createTransactionService.create(new CreateTransactionDTO(fromAccount, TransactionTypeEnum.DEBIT, data.amount(), desc));
        createTransactionService.create(new CreateTransactionDTO(toAccount, TransactionTypeEnum.CREDIT, data.amount(), desc));

        Transfer transfer = new Transfer();
        transfer.setFromAccount(fromAccount);
        transfer.setToAccount(toAccount);
        transfer.setAmount(data.amount());
        transfer.setStatus(TransferStatusEnum.COMPLETED);
        transfer.setDescription(desc);

        Transfer saved = transferRepository.save(transfer);
        log.info("PIX transfer: id={} from={} to={} key={} amount={}", saved.getId(), fromAccount.getId(), toAccount.getId(), data.pixKey(), data.amount());

        incrementPixContactService.increment(fromAccount.getId(), data.pixKey());
        return saved;
    }
}
