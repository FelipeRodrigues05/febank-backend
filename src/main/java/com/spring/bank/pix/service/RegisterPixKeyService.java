package com.spring.bank.pix.service;

import com.spring.bank.account.enums.AccountStatusEnum;
import com.spring.bank.account.model.Account;
import com.spring.bank.account.service.FindAccountService;
import com.spring.bank.common.exception.InvalidAccountException;
import com.spring.bank.common.exception.PixKeyAlreadyExistsException;
import com.spring.bank.common.exception.PixKeyLimitExceededException;
import com.spring.bank.pix.dto.CreatePixKeyDTO;
import com.spring.bank.pix.model.PixKey;
import com.spring.bank.pix.repository.PixKeyRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterPixKeyService {

    private static final Logger log = LoggerFactory.getLogger(RegisterPixKeyService.class);
    private static final int MAX_KEYS_PER_ACCOUNT = 5;

    private final PixKeyRepository pixKeyRepository;
    private final FindAccountService findAccountService;

    public PixKey register(CreatePixKeyDTO data) {
        if (pixKeyRepository.existsByKey(data.key())) {
            throw new PixKeyAlreadyExistsException("PIX key already registered: " + data.key());
        }

        Account account = findAccountService.getById(data.accountId());
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
}
