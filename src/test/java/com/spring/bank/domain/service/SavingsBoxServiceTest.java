package com.spring.bank.domain.service;

import com.spring.bank.common.exception.InsufficientFundsException;
import com.spring.bank.common.exception.InvalidAccountException;
import com.spring.bank.common.exception.SavingsBoxNotFoundException;
import com.spring.bank.domain.dto.savingsbox.CreateSavingsBoxDTO;
import com.spring.bank.domain.dto.savingsbox.SavingsBoxDepositDTO;
import com.spring.bank.domain.dto.savingsbox.SavingsBoxWithdrawDTO;
import com.spring.bank.domain.enums.account.AccountStatusEnum;
import com.spring.bank.domain.enums.account.AccountTypeEnum;
import com.spring.bank.domain.enums.transaction.TransactionTypeEnum;
import com.spring.bank.domain.model.Account;
import com.spring.bank.domain.model.SavingsBox;
import com.spring.bank.domain.repository.SavingsBoxRepository;
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
class SavingsBoxServiceTest {

    @Mock private SavingsBoxRepository savingsBoxRepository;
    @Mock private AccountService accountService;
    @Mock private TransactionService transactionService;

    @InjectMocks private SavingsBoxService savingsBoxService;

    private Account account;
    private SavingsBox savingsBox;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setId(1L);
        account.setType(AccountTypeEnum.CHECKING);
        account.setStatus(AccountStatusEnum.ACTIVE);
        account.setBalance(new BigDecimal("1000.00"));

        savingsBox = new SavingsBox();
        savingsBox.setId(1L);
        savingsBox.setAccount(account);
        savingsBox.setName("Viagem Europa");
        savingsBox.setImageUrl("https://example.com/europa.jpg");
        savingsBox.setBalance(new BigDecimal("500.00"));
    }

    @Test
    void create_shouldCreateBoxWithZeroBalanceWhenNoInitialAmount() {
        CreateSavingsBoxDTO dto = new CreateSavingsBoxDTO(1L, "Viagem", "https://img.com/viagem.jpg", null);
        when(accountService.getById(1L)).thenReturn(account);
        when(savingsBoxRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SavingsBox result = savingsBoxService.create(dto);

        assertThat(result.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getName()).isEqualTo("Viagem");
        verify(accountService, never()).subtractFunds(any(), any());
        verify(transactionService, never()).create(any());
    }

    @Test
    void create_shouldDeductInitialAmountFromAccount() {
        CreateSavingsBoxDTO dto = new CreateSavingsBoxDTO(1L, "Viagem", "https://img.com/viagem.jpg", new BigDecimal("200.00"));
        when(accountService.getById(1L)).thenReturn(account);
        when(savingsBoxRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SavingsBox result = savingsBoxService.create(dto);

        assertThat(result.getBalance()).isEqualByComparingTo(new BigDecimal("200.00"));
        verify(accountService).subtractFunds(1L, new BigDecimal("200.00"));
        verify(transactionService).create(argThat(tx -> tx.type() == TransactionTypeEnum.DEBIT));
    }

    @Test
    void create_shouldThrowWhenAccountIsBlocked() {
        account.setStatus(AccountStatusEnum.BLOCKED);
        CreateSavingsBoxDTO dto = new CreateSavingsBoxDTO(1L, "Viagem", "https://img.com/viagem.jpg", null);
        when(accountService.getById(1L)).thenReturn(account);

        assertThatThrownBy(() -> savingsBoxService.create(dto))
                .isInstanceOf(InvalidAccountException.class);
    }

    @Test
    void deposit_shouldIncreaseBoxBalanceAndDebitAccount() {
        SavingsBoxDepositDTO dto = new SavingsBoxDepositDTO(new BigDecimal("300.00"));
        when(savingsBoxRepository.findById(1L)).thenReturn(Optional.of(savingsBox));
        when(savingsBoxRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SavingsBox result = savingsBoxService.deposit(1L, dto);

        assertThat(result.getBalance()).isEqualByComparingTo(new BigDecimal("800.00"));
        verify(accountService).subtractFunds(1L, new BigDecimal("300.00"));
        verify(transactionService).create(argThat(tx -> tx.type() == TransactionTypeEnum.DEBIT));
    }

    @Test
    void deposit_shouldThrowWhenAccountIsBlocked() {
        account.setStatus(AccountStatusEnum.BLOCKED);
        SavingsBoxDepositDTO dto = new SavingsBoxDepositDTO(new BigDecimal("100.00"));
        when(savingsBoxRepository.findById(1L)).thenReturn(Optional.of(savingsBox));

        assertThatThrownBy(() -> savingsBoxService.deposit(1L, dto))
                .isInstanceOf(InvalidAccountException.class);
    }

    @Test
    void withdraw_shouldDecreaseBoxBalanceAndCreditAccount() {
        SavingsBoxWithdrawDTO dto = new SavingsBoxWithdrawDTO(new BigDecimal("200.00"));
        when(savingsBoxRepository.findById(1L)).thenReturn(Optional.of(savingsBox));
        when(savingsBoxRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SavingsBox result = savingsBoxService.withdraw(1L, dto);

        assertThat(result.getBalance()).isEqualByComparingTo(new BigDecimal("300.00"));
        verify(accountService).addFunds(1L, new BigDecimal("200.00"));
        verify(transactionService).create(argThat(tx -> tx.type() == TransactionTypeEnum.CREDIT));
    }

    @Test
    void withdraw_shouldThrowWhenBoxBalanceInsufficient() {
        SavingsBoxWithdrawDTO dto = new SavingsBoxWithdrawDTO(new BigDecimal("9999.00"));
        when(savingsBoxRepository.findById(1L)).thenReturn(Optional.of(savingsBox));

        assertThatThrownBy(() -> savingsBoxService.withdraw(1L, dto))
                .isInstanceOf(InsufficientFundsException.class);

        verify(accountService, never()).addFunds(any(), any());
    }

    @Test
    void getById_shouldThrowWhenBoxNotFound() {
        when(savingsBoxRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> savingsBoxService.getById(99L))
                .isInstanceOf(SavingsBoxNotFoundException.class);
    }
}
