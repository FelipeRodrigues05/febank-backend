package com.spring.bank.investment.service;

import com.spring.bank.account.model.Account;
import com.spring.bank.account.service.AccountFundsService;
import com.spring.bank.account.service.FindAccountService;
import com.spring.bank.investment.dto.ApplyProductInvestmentDTO;
import com.spring.bank.investment.model.InvestmentPosition;
import com.spring.bank.investment.model.InvestmentProduct;
import com.spring.bank.investment.repository.InvestmentPositionRepository;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplyProductInvestmentServiceTest {

    @Mock private InvestmentPositionRepository investmentPositionRepository;
    @Mock private FindAccountService findAccountService;
    @Mock private AccountFundsService accountFundsService;
    @Mock private CreateTransactionService createTransactionService;
    @Mock private InvestmentProductCatalogService investmentProductCatalogService;

    @InjectMocks private ApplyProductInvestmentService applyProductInvestmentService;

    private Account account;
    private InvestmentProduct product;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setId(1L);
        account.setBalance(new BigDecimal("2000.00"));

        product = new InvestmentProduct();
        product.setId(10L);
        product.setName("CDB 120% CDI");
        product.setMinimumAmount(new BigDecimal("500.00"));
    }

    @Test
    void apply_shouldCreatePositionAndDeductFunds() {
        ApplyProductInvestmentDTO dto = new ApplyProductInvestmentDTO(1L, 10L, new BigDecimal("1000.00"));

        when(findAccountService.getById(1L)).thenReturn(account);
        when(investmentProductCatalogService.getById(10L)).thenReturn(product);
        when(investmentPositionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        InvestmentPosition result = applyProductInvestmentService.apply(dto);

        assertThat(result.getInvestedAmount()).isEqualByComparingTo(new BigDecimal("1000.00"));
        assertThat(result.isActive()).isTrue();
        verify(accountFundsService).subtractFunds(1L, new BigDecimal("1000.00"));
        verify(createTransactionService).create(any());
    }

    @Test
    void apply_shouldThrowWhenInsufficientFunds() {
        ApplyProductInvestmentDTO dto = new ApplyProductInvestmentDTO(1L, 10L, new BigDecimal("100.00"));

        when(findAccountService.getById(1L)).thenReturn(account);
        when(investmentProductCatalogService.getById(10L)).thenReturn(product);

        assertThatThrownBy(() -> applyProductInvestmentService.apply(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Minimum investment");

        verify(accountFundsService, never()).subtractFunds(any(), any());
    }
}
