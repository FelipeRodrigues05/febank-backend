package com.spring.bank.savings.service;

import com.spring.bank.account.enums.AccountStatusEnum;
import com.spring.bank.account.enums.AccountTypeEnum;
import com.spring.bank.account.model.Account;
import com.spring.bank.account.service.AccountFundsService;
import com.spring.bank.common.exception.InsufficientFundsException;
import com.spring.bank.common.exception.SavingsBoxNotFoundException;
import com.spring.bank.savings.dto.SavingsBoxWithdrawDTO;
import com.spring.bank.savings.model.SavingsBox;
import com.spring.bank.savings.repository.SavingsBoxRepository;
import com.spring.bank.transaction.enums.TransactionTypeEnum;
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
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SavingsBoxWithdrawServiceTest {

    @Mock private SavingsBoxRepository savingsBoxRepository;
    @Mock private AccountFundsService accountFundsService;
    @Mock private CreateTransactionService createTransactionService;
    @Mock private FindSavingsBoxService findSavingsBoxService;

    @InjectMocks private SavingsBoxWithdrawService savingsBoxWithdrawService;

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
        savingsBox.setBalance(new BigDecimal("500.00"));
    }

    @Test
    void withdraw_shouldDecreaseBoxBalanceAndCreditAccount() {
        SavingsBoxWithdrawDTO dto = new SavingsBoxWithdrawDTO(new BigDecimal("200.00"));
        when(findSavingsBoxService.getById(1L)).thenReturn(savingsBox);
        when(savingsBoxRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        SavingsBox result = savingsBoxWithdrawService.withdraw(1L, dto);

        assertThat(result.getBalance()).isEqualByComparingTo(new BigDecimal("300.00"));
        verify(accountFundsService).addFunds(1L, new BigDecimal("200.00"));
        verify(createTransactionService).create(argThat(tx -> tx.type() == TransactionTypeEnum.CREDIT));
    }

    @Test
    void withdraw_shouldThrowWhenBoxBalanceInsufficient() {
        SavingsBoxWithdrawDTO dto = new SavingsBoxWithdrawDTO(new BigDecimal("9999.00"));
        when(findSavingsBoxService.getById(1L)).thenReturn(savingsBox);

        assertThatThrownBy(() -> savingsBoxWithdrawService.withdraw(1L, dto))
                .isInstanceOf(InsufficientFundsException.class);

        verify(accountFundsService, never()).addFunds(any(), any());
    }

    @Test
    void getById_shouldThrowWhenBoxNotFound() {
        when(findSavingsBoxService.getById(99L)).thenThrow(new SavingsBoxNotFoundException("Savings box with ID 99 not found"));

        assertThatThrownBy(() -> savingsBoxWithdrawService.withdraw(99L, new SavingsBoxWithdrawDTO(new BigDecimal("10.00"))))
                .isInstanceOf(SavingsBoxNotFoundException.class);
    }
}
