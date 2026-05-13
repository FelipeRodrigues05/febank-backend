package com.spring.bank.savings.service;

import com.spring.bank.account.enums.AccountStatusEnum;
import com.spring.bank.account.enums.AccountTypeEnum;
import com.spring.bank.account.model.Account;
import com.spring.bank.account.service.AccountFundsService;
import com.spring.bank.account.service.FindAccountService;
import com.spring.bank.common.exception.InvalidAccountException;
import com.spring.bank.savings.dto.CreateSavingsBoxDTO;
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
class CreateSavingsBoxServiceTest {

    @Mock private SavingsBoxRepository savingsBoxRepository;
    @Mock private FindAccountService findAccountService;
    @Mock private AccountFundsService accountFundsService;
    @Mock private CreateTransactionService createTransactionService;

    @InjectMocks private CreateSavingsBoxService createSavingsBoxService;

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
    void create_shouldCreateBoxWithZeroBalanceWhenNoInitialAmount() {
        CreateSavingsBoxDTO dto = new CreateSavingsBoxDTO(1L, "Viagem", "https://img.com/viagem.jpg", null);
        when(findAccountService.getById(1L)).thenReturn(account);
        when(savingsBoxRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        SavingsBox result = createSavingsBoxService.create(dto);

        assertThat(result.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getName()).isEqualTo("Viagem");
        verify(accountFundsService, never()).subtractFunds(any(), any());
        verify(createTransactionService, never()).create(any());
    }

    @Test
    void create_shouldDeductInitialAmountFromAccount() {
        CreateSavingsBoxDTO dto = new CreateSavingsBoxDTO(1L, "Viagem", "https://img.com/viagem.jpg", new BigDecimal("200.00"));
        when(findAccountService.getById(1L)).thenReturn(account);
        when(savingsBoxRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        SavingsBox result = createSavingsBoxService.create(dto);

        assertThat(result.getBalance()).isEqualByComparingTo(new BigDecimal("200.00"));
        verify(accountFundsService).subtractFunds(1L, new BigDecimal("200.00"));
        verify(createTransactionService).create(argThat(tx -> tx.type() == TransactionTypeEnum.DEBIT));
    }

    @Test
    void create_shouldThrowWhenAccountIsBlocked() {
        account.setStatus(AccountStatusEnum.BLOCKED);
        CreateSavingsBoxDTO dto = new CreateSavingsBoxDTO(1L, "Viagem", "https://img.com/viagem.jpg", null);
        when(findAccountService.getById(1L)).thenReturn(account);

        assertThatThrownBy(() -> createSavingsBoxService.create(dto))
                .isInstanceOf(InvalidAccountException.class);
    }
}
