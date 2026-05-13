package com.spring.bank.account.service;

import com.spring.bank.account.dto.DepositDTO;
import com.spring.bank.account.dto.WithdrawDTO;
import com.spring.bank.account.enums.AccountStatusEnum;
import com.spring.bank.account.enums.AccountTypeEnum;
import com.spring.bank.account.model.Account;
import com.spring.bank.common.exception.InsufficientFundsException;
import com.spring.bank.common.exception.InvalidAccountException;
import com.spring.bank.transaction.service.CreateTransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountMovementServiceTest {

    @Mock private FindAccountService findAccountService;
    @Mock private CreateTransactionService createTransactionService;

    @InjectMocks private AccountMovementService accountMovementService;

    private Account checkingAccount;
    private Account savingsAccount;

    @BeforeEach
    void setUp() {
        checkingAccount = new Account();
        checkingAccount.setId(1L);
        checkingAccount.setType(AccountTypeEnum.CHECKING);
        checkingAccount.setStatus(AccountStatusEnum.ACTIVE);
        checkingAccount.setBalance(new BigDecimal("1000.00"));

        savingsAccount = new Account();
        savingsAccount.setId(2L);
        savingsAccount.setType(AccountTypeEnum.SAVINGS);
        savingsAccount.setStatus(AccountStatusEnum.ACTIVE);
        savingsAccount.setBalance(new BigDecimal("500.00"));
    }

    @Test
    void deposit_shouldCreateTwoTransactionsAndReturnSavingsAccount() {
        DepositDTO dto = new DepositDTO(1L, new BigDecimal("200.00"));
        when(findAccountService.getFirstByUserAndType(1L, AccountTypeEnum.CHECKING)).thenReturn(checkingAccount);
        when(findAccountService.getFirstByUserAndType(1L, AccountTypeEnum.SAVINGS)).thenReturn(savingsAccount);

        Account result = accountMovementService.deposit(dto);

        assertThat(result).isEqualTo(savingsAccount);
        verify(createTransactionService, times(2)).create(any());
    }

    @Test
    void deposit_shouldThrowWhenCheckingAccountIsBlocked() {
        checkingAccount.setStatus(AccountStatusEnum.BLOCKED);
        DepositDTO dto = new DepositDTO(1L, new BigDecimal("200.00"));
        when(findAccountService.getFirstByUserAndType(1L, AccountTypeEnum.CHECKING)).thenReturn(checkingAccount);
        when(findAccountService.getFirstByUserAndType(1L, AccountTypeEnum.SAVINGS)).thenReturn(savingsAccount);

        assertThatThrownBy(() -> accountMovementService.deposit(dto))
                .isInstanceOf(InvalidAccountException.class);
    }

    @Test
    void deposit_shouldThrowWhenSavingsAccountIsBlocked() {
        savingsAccount.setStatus(AccountStatusEnum.BLOCKED);
        DepositDTO dto = new DepositDTO(1L, new BigDecimal("200.00"));
        when(findAccountService.getFirstByUserAndType(1L, AccountTypeEnum.CHECKING)).thenReturn(checkingAccount);
        when(findAccountService.getFirstByUserAndType(1L, AccountTypeEnum.SAVINGS)).thenReturn(savingsAccount);

        assertThatThrownBy(() -> accountMovementService.deposit(dto))
                .isInstanceOf(InvalidAccountException.class);
    }

    @Test
    void deposit_shouldThrowWhenCheckingBalanceInsufficient() {
        DepositDTO dto = new DepositDTO(1L, new BigDecimal("5000.00"));
        when(findAccountService.getFirstByUserAndType(1L, AccountTypeEnum.CHECKING)).thenReturn(checkingAccount);
        when(findAccountService.getFirstByUserAndType(1L, AccountTypeEnum.SAVINGS)).thenReturn(savingsAccount);

        assertThatThrownBy(() -> accountMovementService.deposit(dto))
                .isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    void withdraw_shouldCreateTwoTransactionsAndReturnCheckingAccount() {
        WithdrawDTO dto = new WithdrawDTO(1L, new BigDecimal("300.00"));
        when(findAccountService.getFirstByUserAndType(1L, AccountTypeEnum.CHECKING)).thenReturn(checkingAccount);
        when(findAccountService.getFirstByUserAndType(1L, AccountTypeEnum.SAVINGS)).thenReturn(savingsAccount);

        Account result = accountMovementService.withdraw(dto);

        assertThat(result).isEqualTo(checkingAccount);
        verify(createTransactionService, times(2)).create(any());
    }

    @Test
    void withdraw_shouldThrowWhenSavingsBalanceInsufficient() {
        WithdrawDTO dto = new WithdrawDTO(1L, new BigDecimal("1000.00"));
        when(findAccountService.getFirstByUserAndType(1L, AccountTypeEnum.CHECKING)).thenReturn(checkingAccount);
        when(findAccountService.getFirstByUserAndType(1L, AccountTypeEnum.SAVINGS)).thenReturn(savingsAccount);

        assertThatThrownBy(() -> accountMovementService.withdraw(dto))
                .isInstanceOf(InsufficientFundsException.class);
    }
}
