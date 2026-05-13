package com.spring.bank.loan.service;

import com.spring.bank.account.enums.AccountStatusEnum;
import com.spring.bank.account.enums.AccountTypeEnum;
import com.spring.bank.account.model.Account;
import com.spring.bank.account.service.AccountFundsService;
import com.spring.bank.account.service.FindAccountService;
import com.spring.bank.loan.dto.ApplyLoanDTO;
import com.spring.bank.loan.enums.LoanStatus;
import com.spring.bank.loan.enums.LoanType;
import com.spring.bank.loan.model.Loan;
import com.spring.bank.loan.repository.LoanInstallmentRepository;
import com.spring.bank.loan.repository.LoanRepository;
import com.spring.bank.transaction.service.CreateTransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplyLoanServiceTest {

    @Mock private LoanRepository loanRepository;
    @Mock private LoanInstallmentRepository loanInstallmentRepository;
    @Mock private FindAccountService findAccountService;
    @Mock private SimulateLoanService simulateLoanService;
    @Mock private AccountFundsService accountFundsService;
    @Mock private CreateTransactionService createTransactionService;

    @InjectMocks private ApplyLoanService applyLoanService;

    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setId(1L);
        account.setType(AccountTypeEnum.CHECKING);
        account.setStatus(AccountStatusEnum.ACTIVE);
        account.setBalance(new BigDecimal("10000.00"));
        account.setCreatedAt(LocalDateTime.now().minusMonths(12));
    }

    @Test
    void apply_shouldApproveAndDisburseFunds() {
        ApplyLoanDTO dto = new ApplyLoanDTO(1L, LoanType.PERSONAL, new BigDecimal("5000.00"), 12);

        Loan savedLoan = new Loan();
        savedLoan.setId(1L);
        savedLoan.setStatus(LoanStatus.ACTIVE);

        when(findAccountService.getById(1L)).thenReturn(account);
        when(simulateLoanService.getRate(anyInt())).thenReturn(new BigDecimal("0.025"));
        when(simulateLoanService.calculateInstallment(any(), any(), anyInt())).thenReturn(new BigDecimal("950.00"));
        when(loanRepository.save(any())).thenReturn(savedLoan);

        Loan result = applyLoanService.apply(dto);

        assertThat(result.getStatus()).isEqualTo(LoanStatus.ACTIVE);
        verify(accountFundsService).addFunds(1L, new BigDecimal("5000.00"));
        verify(createTransactionService).create(any());
    }

    @Test
    void apply_shouldRejectWhenAccountBlocked() {
        account.setStatus(AccountStatusEnum.BLOCKED);
        ApplyLoanDTO dto = new ApplyLoanDTO(1L, LoanType.PERSONAL, new BigDecimal("5000.00"), 12);

        when(findAccountService.getById(1L)).thenReturn(account);

        assertThatThrownBy(() -> applyLoanService.apply(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not active");
    }

    @Test
    void apply_shouldRejectWhenAccountNotChecking() {
        account.setType(AccountTypeEnum.SAVINGS);
        ApplyLoanDTO dto = new ApplyLoanDTO(1L, LoanType.PERSONAL, new BigDecimal("5000.00"), 12);

        when(findAccountService.getById(1L)).thenReturn(account);

        assertThatThrownBy(() -> applyLoanService.apply(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("checking accounts");
    }
}
