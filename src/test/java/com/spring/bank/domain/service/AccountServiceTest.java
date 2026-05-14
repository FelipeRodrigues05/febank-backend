package com.spring.bank.domain.service;

import com.spring.bank.common.exception.AccountTypeAlreadyExistsException;
import com.spring.bank.common.exception.InsufficientFundsException;
import com.spring.bank.common.exception.InvalidAccountException;
import com.spring.bank.common.utils.AccountNumberGenerator;
import com.spring.bank.domain.dto.account.DepositDTO;
import com.spring.bank.domain.dto.account.OpenAccountDTO;
import com.spring.bank.domain.dto.account.WithdrawDTO;
import com.spring.bank.domain.enums.account.AccountStatusEnum;
import com.spring.bank.domain.enums.account.AccountTypeEnum;
import com.spring.bank.domain.model.Account;
import com.spring.bank.domain.model.User;
import com.spring.bank.domain.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock private AccountRepository accountRepository;
    @Mock private AccountNumberGenerator accountNumberGenerator;
    @Mock private TransactionService transactionService;
    @Mock private UserService userService;

    @InjectMocks private AccountService accountService;

    private User user;
    private Account checkingAccount;
    private Account savingsAccount;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John Doe");

        checkingAccount = new Account();
        checkingAccount.setId(1L);
        checkingAccount.setUser(user);
        checkingAccount.setType(AccountTypeEnum.CHECKING);
        checkingAccount.setStatus(AccountStatusEnum.ACTIVE);
        checkingAccount.setBalance(new BigDecimal("1000.00"));
        checkingAccount.setNumber("10000001");

        savingsAccount = new Account();
        savingsAccount.setId(2L);
        savingsAccount.setUser(user);
        savingsAccount.setType(AccountTypeEnum.SAVINGS);
        savingsAccount.setStatus(AccountStatusEnum.ACTIVE);
        savingsAccount.setBalance(new BigDecimal("500.00"));
        savingsAccount.setNumber("10000002");
    }

    @Test
    void openAccount_shouldCreateWithZeroBalanceAndActiveStatus() {
        OpenAccountDTO dto = new OpenAccountDTO(1L, AccountTypeEnum.CHECKING);
        when(userService.getById(1L)).thenReturn(user);
        when(accountRepository.existsByUserIdAndType(1L, AccountTypeEnum.CHECKING)).thenReturn(false);
        when(accountNumberGenerator.generateAccount()).thenReturn("10000001");
        when(accountRepository.existsByNumber("10000001")).thenReturn(false);
        when(accountRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Account result = accountService.openAccount(dto);

        assertThat(result.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getStatus()).isEqualTo(AccountStatusEnum.ACTIVE);
        assertThat(result.getType()).isEqualTo(AccountTypeEnum.CHECKING);
    }

    @Test
    void openAccount_shouldRetryUntilUniqueNumberIsFound() {
        OpenAccountDTO dto = new OpenAccountDTO(1L, AccountTypeEnum.CHECKING);
        when(userService.getById(1L)).thenReturn(user);
        when(accountRepository.existsByUserIdAndType(1L, AccountTypeEnum.CHECKING)).thenReturn(false);
        when(accountNumberGenerator.generateAccount()).thenReturn("DUP", "UNIQUE");
        when(accountRepository.existsByNumber("DUP")).thenReturn(true);
        when(accountRepository.existsByNumber("UNIQUE")).thenReturn(false);
        when(accountRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Account result = accountService.openAccount(dto);

        assertThat(result.getNumber()).isEqualTo("UNIQUE");
        verify(accountNumberGenerator, times(2)).generateAccount();
    }

    @Test
    void openAccount_shouldThrowWhenUserAlreadyHasThatAccountType() {
        OpenAccountDTO dto = new OpenAccountDTO(1L, AccountTypeEnum.CHECKING);
        when(userService.getById(1L)).thenReturn(user);
        when(accountRepository.existsByUserIdAndType(1L, AccountTypeEnum.CHECKING)).thenReturn(true);

        assertThatThrownBy(() -> accountService.openAccount(dto))
                .isInstanceOf(AccountTypeAlreadyExistsException.class)
                .hasMessageContaining("CHECKING");
    }

    @Test
    void deposit_shouldQueueTwoTransactionsAndReturnSavingsAccount() {
        DepositDTO dto = new DepositDTO(1L, new BigDecimal("200.00"));
        when(accountRepository.findFirstByUserIdAndType(1L, AccountTypeEnum.CHECKING)).thenReturn(Optional.of(checkingAccount));
        when(accountRepository.findFirstByUserIdAndType(1L, AccountTypeEnum.SAVINGS)).thenReturn(Optional.of(savingsAccount));

        Account result = accountService.deposit(dto);

        assertThat(result).isEqualTo(savingsAccount);
        verify(transactionService, times(2)).create(any());
    }

    @Test
    void deposit_shouldThrowWhenCheckingAccountIsBlocked() {
        checkingAccount.setStatus(AccountStatusEnum.BLOCKED);
        DepositDTO dto = new DepositDTO(1L, new BigDecimal("200.00"));
        when(accountRepository.findFirstByUserIdAndType(1L, AccountTypeEnum.CHECKING)).thenReturn(Optional.of(checkingAccount));
        when(accountRepository.findFirstByUserIdAndType(1L, AccountTypeEnum.SAVINGS)).thenReturn(Optional.of(savingsAccount));

        assertThatThrownBy(() -> accountService.deposit(dto))
                .isInstanceOf(InvalidAccountException.class)
                .hasMessageContaining("not active");
    }

    @Test
    void deposit_shouldThrowWhenSavingsAccountIsBlocked() {
        savingsAccount.setStatus(AccountStatusEnum.BLOCKED);
        DepositDTO dto = new DepositDTO(1L, new BigDecimal("200.00"));
        when(accountRepository.findFirstByUserIdAndType(1L, AccountTypeEnum.CHECKING)).thenReturn(Optional.of(checkingAccount));
        when(accountRepository.findFirstByUserIdAndType(1L, AccountTypeEnum.SAVINGS)).thenReturn(Optional.of(savingsAccount));

        assertThatThrownBy(() -> accountService.deposit(dto))
                .isInstanceOf(InvalidAccountException.class);
    }

    @Test
    void deposit_shouldThrowWhenCheckingBalanceInsufficient() {
        DepositDTO dto = new DepositDTO(1L, new BigDecimal("5000.00"));
        when(accountRepository.findFirstByUserIdAndType(1L, AccountTypeEnum.CHECKING)).thenReturn(Optional.of(checkingAccount));
        when(accountRepository.findFirstByUserIdAndType(1L, AccountTypeEnum.SAVINGS)).thenReturn(Optional.of(savingsAccount));

        assertThatThrownBy(() -> accountService.deposit(dto))
                .isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    void withdraw_shouldQueueTwoTransactionsAndReturnCheckingAccount() {
        WithdrawDTO dto = new WithdrawDTO(1L, new BigDecimal("300.00"));
        when(accountRepository.findFirstByUserIdAndType(1L, AccountTypeEnum.CHECKING)).thenReturn(Optional.of(checkingAccount));
        when(accountRepository.findFirstByUserIdAndType(1L, AccountTypeEnum.SAVINGS)).thenReturn(Optional.of(savingsAccount));

        Account result = accountService.withdraw(dto);

        assertThat(result).isEqualTo(checkingAccount);
        verify(transactionService, times(2)).create(any());
    }

    @Test
    void withdraw_shouldThrowWhenSavingsBalanceInsufficient() {
        WithdrawDTO dto = new WithdrawDTO(1L, new BigDecimal("1000.00"));
        when(accountRepository.findFirstByUserIdAndType(1L, AccountTypeEnum.CHECKING)).thenReturn(Optional.of(checkingAccount));
        when(accountRepository.findFirstByUserIdAndType(1L, AccountTypeEnum.SAVINGS)).thenReturn(Optional.of(savingsAccount));

        assertThatThrownBy(() -> accountService.withdraw(dto))
                .isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    void subtractFunds_shouldThrowWhenAmountIsZero() {
        assertThatThrownBy(() -> accountService.subtractFunds(1L, BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void subtractFunds_shouldThrowWhenInsufficientBalance() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(checkingAccount));

        assertThatThrownBy(() -> accountService.subtractFunds(1L, new BigDecimal("9999.00")))
                .isInstanceOf(InsufficientFundsException.class);
    }
}
