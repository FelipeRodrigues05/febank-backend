package com.spring.bank.investment.service;

import com.spring.bank.account.enums.AccountStatusEnum;
import com.spring.bank.account.enums.AccountTypeEnum;
import com.spring.bank.account.model.Account;
import com.spring.bank.account.repository.AccountRepository;
import com.spring.bank.common.utils.IncomeCalculator;
import com.spring.bank.transaction.enums.TransactionTypeEnum;
import com.spring.bank.transaction.service.CreateTransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DailyEarningsServiceTest {

    @Mock private AccountRepository accountRepository;
    @Mock private CreateTransactionService createTransactionService;
    @Mock private IncomeCalculator incomeCalculator;

    @InjectMocks private DailyEarningsService dailyEarningsService;

    @Test
    void processEarnings_shouldApplyEarningsToActiveAccounts() {
        Account activeInvestment = new Account();
        activeInvestment.setId(10L);
        activeInvestment.setStatus(AccountStatusEnum.ACTIVE);
        activeInvestment.setBalance(new BigDecimal("1000.00"));

        when(accountRepository.findAllByTypeAndStatus(AccountTypeEnum.INVESTMENT, AccountStatusEnum.ACTIVE))
                .thenReturn(List.of(activeInvestment));
        when(incomeCalculator.daily()).thenReturn(0.001);

        dailyEarningsService.processEarnings();

        verify(createTransactionService).create(argThat(dto -> dto.type() == TransactionTypeEnum.FEE));
        verify(accountRepository).saveAll(any());
    }

    @Test
    void processEarnings_shouldSkipAccountsWithZeroBalance() {
        Account emptyInvestment = new Account();
        emptyInvestment.setId(10L);
        emptyInvestment.setStatus(AccountStatusEnum.ACTIVE);
        emptyInvestment.setBalance(BigDecimal.ZERO);

        when(accountRepository.findAllByTypeAndStatus(AccountTypeEnum.INVESTMENT, AccountStatusEnum.ACTIVE))
                .thenReturn(List.of(emptyInvestment));

        dailyEarningsService.processEarnings();

        verify(createTransactionService, never()).create(any());
    }

    @Test
    void processEarnings_shouldSkipWhenNoActiveAccounts() {
        when(accountRepository.findAllByTypeAndStatus(AccountTypeEnum.INVESTMENT, AccountStatusEnum.ACTIVE))
                .thenReturn(List.of());

        dailyEarningsService.processEarnings();

        verify(createTransactionService, never()).create(any());
        verify(accountRepository, never()).saveAll(any());
    }
}
