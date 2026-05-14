package com.spring.bank.account.service;

import com.spring.bank.account.dto.OpenAccountDTO;
import com.spring.bank.account.enums.AccountStatusEnum;
import com.spring.bank.account.model.Account;
import com.spring.bank.account.repository.AccountRepository;
import com.spring.bank.common.exception.AccountTypeAlreadyExistsException;
import com.spring.bank.common.utils.AccountNumberGenerator;
import com.spring.bank.user.model.User;
import com.spring.bank.user.service.FindUserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OpenAccountService {

    private static final Logger log = LoggerFactory.getLogger(OpenAccountService.class);

    private final AccountRepository accountRepository;
    private final AccountNumberGenerator accountNumberGenerator;
    private final FindUserService findUserService;

    public Account open(OpenAccountDTO data) {
        User user = findUserService.getById(data.userId());

        if (accountRepository.existsByUserIdAndType(user.getId(), data.type())) {
            throw new AccountTypeAlreadyExistsException(
                    String.format("User already has a %s account", data.type())
            );
        }

        Account account = new Account();
        account.setBalance(BigDecimal.ZERO);
        account.setUser(user);
        account.setType(data.type());
        account.setStatus(AccountStatusEnum.ACTIVE);

        String generatedNumber;
        do {
            generatedNumber = accountNumberGenerator.generateAccount();
        } while (accountRepository.existsByNumber(generatedNumber));

        account.setNumber(generatedNumber);

        Account saved = accountRepository.save(account);
        log.info("Account opened: type={} userId={} accountId={}", data.type(), data.userId(), saved.getId());
        return saved;
    }
}
