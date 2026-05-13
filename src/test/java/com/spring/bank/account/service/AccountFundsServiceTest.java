package com.spring.bank.account.service;

import com.spring.bank.account.enums.AccountStatusEnum;
import com.spring.bank.account.enums.AccountTypeEnum;
import com.spring.bank.account.model.Account;
import com.spring.bank.account.repository.AccountRepository;
import com.spring.bank.common.exception.InsufficientFundsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountFundsServiceTest {

    @Mock private AccountRepository accountRepository;
    @Mock private FindAccountService findAccountService;

    @InjectMocks private AccountFundsService accountFundsService;

    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setId(1L);
        account.setType(AccountTypeEnum.CHECKING);
        account.setStatus(AccountStatusEnum.ACTIVE);
        account.setBalance(new BigDecimal("1000.00"));
    }

    @Test
    void subtractFunds_shouldThrowWhenAmountIsZero() {
        assertThatThrownBy(() -> accountFundsService.subtractFunds(1L, BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void subtractFunds_shouldThrowWhenAmountIsNegative() {
        assertThatThrownBy(() -> accountFundsService.subtractFunds(1L, new BigDecimal("-10.00")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void subtractFunds_shouldThrowWhenInsufficientBalance() {
        when(findAccountService.getById(1L)).thenReturn(account);

        assertThatThrownBy(() -> accountFundsService.subtractFunds(1L, new BigDecimal("9999.00")))
                .isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    void addFunds_shouldThrowWhenAmountIsZero() {
        assertThatThrownBy(() -> accountFundsService.addFunds(1L, BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
