package com.spring.bank.domain.service;

import com.spring.bank.common.exception.*;
import com.spring.bank.domain.dto.pix.CreatePixKeyDTO;
import com.spring.bank.domain.dto.pix.PixTransferDTO;
import com.spring.bank.domain.dto.transaction.CreateTransactionDTO;
import com.spring.bank.domain.dto.transfer.CreateTransferDTO;
import com.spring.bank.domain.enums.account.AccountStatusEnum;
import com.spring.bank.domain.enums.account.AccountTypeEnum;
import com.spring.bank.domain.enums.transaction.TransactionTypeEnum;
import com.spring.bank.domain.enums.transfer.TransferStatusEnum;
import com.spring.bank.domain.model.Account;
import com.spring.bank.domain.model.PixKey;
import com.spring.bank.domain.model.Transfer;
import com.spring.bank.domain.repository.PixKeyRepository;
import com.spring.bank.domain.repository.TransferRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PixService {

    private static final Logger log = LoggerFactory.getLogger(PixService.class);
    private static final int MAX_KEYS_PER_ACCOUNT = 5;

    private final PixKeyRepository pixKeyRepository;
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final TransferRepository transferRepository;

    public PixKey registerKey(CreatePixKeyDTO data) {
        if (pixKeyRepository.existsByKey(data.key())) {
            throw new PixKeyAlreadyExistsException("PIX key already registered: " + data.key());
        }

        Account account = accountService.getById(data.accountId());
        if (account.getStatus() != AccountStatusEnum.ACTIVE) {
            throw new InvalidAccountException("Account is not active.");
        }
        if (pixKeyRepository.countByAccountId(data.accountId()) >= MAX_KEYS_PER_ACCOUNT) {
            throw new PixKeyLimitExceededException("Account already has the maximum of 5 PIX keys.");
        }

        PixKey pixKey = new PixKey();
        pixKey.setAccount(account);
        pixKey.setType(data.type());
        pixKey.setKey(data.key());

        PixKey saved = pixKeyRepository.save(pixKey);
        log.info("PIX key registered: id={} type={} accountId={}", saved.getId(), data.type(), data.accountId());
        return saved;
    }

    public void deleteKey(String pixKeyId) {
        PixKey pixKey = getById(pixKeyId);
        pixKeyRepository.delete(pixKey);
        log.info("PIX key deleted: id={}", pixKeyId);
    }

    public List<PixKey> listByAccount(Long accountId) {
        return pixKeyRepository.findAllByAccountId(accountId);
    }

    public PixKey getById(String id) {
        return pixKeyRepository.findById(id).orElseThrow(() ->
                new PixKeyNotFoundException("PIX key with ID " + id + " not found"));
    }

    @Transactional
    public Transfer transfer(PixTransferDTO data) {
        Account fromAccount = accountService.getById(data.fromAccountId());
        PixKey pixKey = pixKeyRepository.findByKey(data.pixKey())
                .orElseThrow(() -> new PixKeyNotFoundException("PIX key not found: " + data.pixKey()));
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

        accountService.subtractFunds(fromAccount.getId(), data.amount());
        accountService.addFunds(toAccount.getId(), data.amount());

        String desc = data.description() != null ? data.description() : "PIX: " + data.pixKey();
        transactionService.create(new CreateTransactionDTO(fromAccount, TransactionTypeEnum.DEBIT, data.amount(), desc));
        transactionService.create(new CreateTransactionDTO(toAccount, TransactionTypeEnum.CREDIT, data.amount(), desc));

        Transfer transfer = new Transfer();
        transfer.setFromAccount(fromAccount);
        transfer.setToAccount(toAccount);
        transfer.setAmount(data.amount());
        transfer.setStatus(TransferStatusEnum.COMPLETED);
        transfer.setDescription(desc);

        Transfer saved = transferRepository.save(transfer);
        log.info("PIX transfer: id={} from={} to={} key={} amount={}", saved.getId(), fromAccount.getId(), toAccount.getId(), data.pixKey(), data.amount());
        return saved;
    }
}
