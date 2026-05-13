package com.spring.bank.investment.service;

import com.spring.bank.account.enums.AccountStatusEnum;
import com.spring.bank.account.enums.AccountTypeEnum;
import com.spring.bank.account.model.Account;
import com.spring.bank.account.service.AccountFundsService;
import com.spring.bank.account.service.FindAccountService;
import com.spring.bank.common.exception.InsufficientFundsException;
import com.spring.bank.investment.dto.ApplyInvestmentDTO;
import com.spring.bank.transaction.service.CreateTransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplyInvestmentServiceTest {

    @Mock private FindAccountService findAccountService;
    @Mock private AccountFundsService accountFundsService;
    @Mock private CreateTransactionService createTransactionService;

    @InjectMocks private ApplyInvestmentService applyInvestmentService;

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
    void apply_shouldTransferFromCheckingToInvestment() {
        ApplyInvestmentDTO dto = new ApplyInvestmentDTO(1L, new BigDecimal("500.00"));
        when(findAccountService.getFirstByUserAndType(1L, AccountTypeEnum.CHECKING)).thenReturn(checkingAccount);
        when(findAccountService.getFirstByUserAndType(1L, AccountTypeEnum.INVESTMENT)).thenReturn(investmentAccount);
        when(findAccountService.getById(2L)).thenReturn(investmentAccount);

        applyInvestmentService.apply(dto);

        verify(accountFundsService).subtractFunds(1L, new BigDecimal("500.00"));
        verify(accountFundsService).addFunds(2L, new BigDecimal("500.00"));
        verify(createTransactionService, times(2)).create(any());
    }

    @Test
    void apply_shouldThrowWhenCheckingBalanceInsufficient() {
        ApplyInvestmentDTO dto = new ApplyInvestmentDTO(1L, new BigDecimal("9999.00"));
        when(findAccountService.getFirstByUserAndType(1L, AccountTypeEnum.CHECKING)).thenReturn(checkingAccount);
        when(findAccountService.getFirstByUserAndType(1L, AccountTypeEnum.INVESTMENT)).thenReturn(investmentAccount);

        assertThatThrownBy(() -> applyInvestmentService.apply(dto))
                .isInstanceOf(InsufficientFundsException.class);

        verify(accountFundsService, never()).subtractFunds(any(), any());
    }
}
