package com.spring.bank.domain.service;

import com.spring.bank.common.exception.InsufficientFundsException;
import com.spring.bank.common.utils.IncomeCalculator;
import com.spring.bank.domain.dto.investment.ApplyInvestmentDTO;
import com.spring.bank.domain.dto.investment.RedeemInvestmentDTO;
import com.spring.bank.domain.enums.account.AccountStatusEnum;
import com.spring.bank.domain.enums.account.AccountTypeEnum;
import com.spring.bank.domain.enums.transaction.TransactionTypeEnum;
import com.spring.bank.domain.model.Account;
import com.spring.bank.domain.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvestmentServiceTest {

    @Mock private AccountRepository accountRepository;
    @Mock private TransactionService transactionService;
    @Mock private IncomeCalculator incomeCalculator;
    @Mock private AccountService accountService;

    @InjectMocks private InvestmentService investmentService;

    private Account checkingAccount;
    private Account investmentAccount;

    @BeforeEach
    void setUp() {
        checkingAccount = new Account();
        checkingAccount.setId(1L);
        checkingAccount.setType(AccountTypeEnum.CHECKING);
        checkingAccount.setStatus(AccountStatusEnum.ACTIVE);
        checkingAccount.setBalance(new BigDecimal("2000.00"));

        investmentAccount = new Account();
        investmentAccount.setId(2L);
        investmentAccount.setType(AccountTypeEnum.INVESTMENT);
        investmentAccount.setStatus(AccountStatusEnum.ACTIVE);
        investmentAccount.setBalance(new BigDecimal("500.00"));
    }

    @Test
    void apply_shouldMoveMoneyFromCheckingToInvestment() {
        ApplyInvestmentDTO dto = new ApplyInvestmentDTO(1L, new BigDecimal("500.00"));
        when(accountService.getFirstByUser(1L, AccountTypeEnum.CHECKING)).thenReturn(checkingAccount);
        when(accountService.getFirstByUser(1L, AccountTypeEnum.INVESTMENT)).thenReturn(investmentAccount);
        when(accountRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        investmentService.apply(dto);

        verify(accountService).subtractFunds(1L, new BigDecimal("500.00"));
        verify(accountService).addFunds(2L, new BigDecimal("500.00"));
        verify(transactionService, times(2)).create(any());
    }

    @Test
    void apply_shouldThrowWhenCheckingBalanceInsufficient() {
        ApplyInvestmentDTO dto = new ApplyInvestmentDTO(1L, new BigDecimal("9999.00"));
        when(accountService.getFirstByUser(1L, AccountTypeEnum.CHECKING)).thenReturn(checkingAccount);
        when(accountService.getFirstByUser(1L, AccountTypeEnum.INVESTMENT)).thenReturn(investmentAccount);

        assertThatThrownBy(() -> investmentService.apply(dto))
                .isInstanceOf(InsufficientFundsException.class);

        verify(accountService, never()).subtractFunds(any(), any());
    }

    @Test
    void redeem_shouldMoveMoneyFromInvestmentToChecking() {
        RedeemInvestmentDTO dto = new RedeemInvestmentDTO(1L, new BigDecimal("200.00"));
        when(accountService.getFirstByUser(1L, AccountTypeEnum.INVESTMENT)).thenReturn(investmentAccount);
        when(accountService.getFirstByUser(1L, AccountTypeEnum.CHECKING)).thenReturn(checkingAccount);
        when(accountService.getById(2L)).thenReturn(investmentAccount);

        investmentService.redeem(dto);

        verify(accountService).subtractFunds(2L, new BigDecimal("200.00"));
        verify(accountService).addFunds(1L, new BigDecimal("200.00"));
        verify(transactionService, times(2)).create(any());
    }

    @Test
    void redeem_shouldThrowWhenInvestmentBalanceInsufficient() {
        RedeemInvestmentDTO dto = new RedeemInvestmentDTO(1L, new BigDecimal("9999.00"));
        when(accountService.getFirstByUser(1L, AccountTypeEnum.INVESTMENT)).thenReturn(investmentAccount);
        when(accountService.getFirstByUser(1L, AccountTypeEnum.CHECKING)).thenReturn(checkingAccount);

        assertThatThrownBy(() -> investmentService.redeem(dto))
                .isInstanceOf(InsufficientFundsException.class);

        verify(accountService, never()).subtractFunds(any(), any());
    }

    @Test
    void processDailyEarnings_shouldApplyEarningsToActiveAccounts() {
        Account activeInvestment = new Account();
        activeInvestment.setId(10L);
        activeInvestment.setStatus(AccountStatusEnum.ACTIVE);
        activeInvestment.setBalance(new BigDecimal("1000.00"));

        when(accountRepository.findAllByTypeAndStatus(AccountTypeEnum.INVESTMENT, AccountStatusEnum.ACTIVE))
                .thenReturn(List.of(activeInvestment));
        when(incomeCalculator.daily()).thenReturn(0.001);

        investmentService.processDailyEarnings();

        verify(transactionService).create(argThat(dto -> dto.type() == TransactionTypeEnum.FEE));
        verify(accountRepository).saveAll(any());
    }

    @Test
    void processDailyEarnings_shouldSkipAccountsWithZeroBalance() {
        Account emptyInvestment = new Account();
        emptyInvestment.setId(10L);
        emptyInvestment.setStatus(AccountStatusEnum.ACTIVE);
        emptyInvestment.setBalance(BigDecimal.ZERO);

        when(accountRepository.findAllByTypeAndStatus(AccountTypeEnum.INVESTMENT, AccountStatusEnum.ACTIVE))
                .thenReturn(List.of(emptyInvestment));

        investmentService.processDailyEarnings();

        verify(transactionService, never()).create(any());
    }

    @Test
    void processDailyEarnings_shouldSkipWhenNoActiveAccounts() {
        when(accountRepository.findAllByTypeAndStatus(AccountTypeEnum.INVESTMENT, AccountStatusEnum.ACTIVE))
                .thenReturn(List.of());

        investmentService.processDailyEarnings();

        verify(transactionService, never()).create(any());
        verify(accountRepository, never()).saveAll(any());
    }
}
