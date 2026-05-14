package com.spring.bank.boleto.service;

import com.spring.bank.account.model.Account;
import com.spring.bank.account.service.AccountFundsService;
import com.spring.bank.account.service.FindAccountService;
import com.spring.bank.boleto.dto.PayBoletoDTO;
import com.spring.bank.boleto.enums.BoletoStatus;
import com.spring.bank.boleto.model.Boleto;
import com.spring.bank.boleto.repository.BoletoRepository;
import com.spring.bank.common.exception.InsufficientFundsException;
import com.spring.bank.transaction.service.CreateTransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PayBoletoServiceTest {

    @Mock private BoletoRepository boletoRepository;
    @Mock private FindAccountService findAccountService;
    @Mock private AccountFundsService accountFundsService;
    @Mock private CreateTransactionService createTransactionService;

    @InjectMocks private PayBoletoService payBoletoService;

    private Boleto boleto;
    private Account payerAccount;
    private Account issuerAccount;

    @BeforeEach
    void setUp() {
        boleto = new Boleto();
        boleto.setBoletoCode("TEST-CODE");
        boleto.setIssuerAccountId(2L);
        boleto.setAmount(new BigDecimal("500.00"));
        boleto.setStatus(BoletoStatus.PENDING);
        boleto.setDueDate(LocalDate.now().plusDays(5));

        payerAccount = new Account();
        payerAccount.setId(1L);
        payerAccount.setBalance(new BigDecimal("1000.00"));

        issuerAccount = new Account();
        issuerAccount.setId(2L);
    }

    @Test
    void pay_shouldDebitPayerAndCreditIssuer() {
        PayBoletoDTO dto = new PayBoletoDTO(1L, "TEST-CODE");

        when(boletoRepository.findByBoletoCode("TEST-CODE")).thenReturn(Optional.of(boleto));
        when(findAccountService.getById(1L)).thenReturn(payerAccount);
        when(findAccountService.getById(2L)).thenReturn(issuerAccount);
        when(boletoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Boleto result = payBoletoService.pay(dto);

        assertThat(result.getStatus()).isEqualTo(BoletoStatus.PAID);
        verify(accountFundsService).subtractFunds(1L, new BigDecimal("500.00"));
        verify(accountFundsService).addFunds(2L, new BigDecimal("500.00"));
    }

    @Test
    void pay_shouldThrowWhenAlreadyPaid() {
        boleto.setStatus(BoletoStatus.PAID);
        PayBoletoDTO dto = new PayBoletoDTO(1L, "TEST-CODE");

        when(boletoRepository.findByBoletoCode("TEST-CODE")).thenReturn(Optional.of(boleto));

        assertThatThrownBy(() -> payBoletoService.pay(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not payable");
    }

    @Test
    void pay_shouldThrowWhenExpired() {
        boleto.setDueDate(LocalDate.now().minusDays(1));
        PayBoletoDTO dto = new PayBoletoDTO(1L, "TEST-CODE");

        when(boletoRepository.findByBoletoCode("TEST-CODE")).thenReturn(Optional.of(boleto));

        assertThatThrownBy(() -> payBoletoService.pay(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("expired");
    }

    @Test
    void pay_shouldThrowWhenInsufficientFunds() {
        payerAccount.setBalance(new BigDecimal("100.00"));
        boleto.setAmount(new BigDecimal("500.00"));
        PayBoletoDTO dto = new PayBoletoDTO(1L, "TEST-CODE");

        when(boletoRepository.findByBoletoCode("TEST-CODE")).thenReturn(Optional.of(boleto));
        when(findAccountService.getById(1L)).thenReturn(payerAccount);

        assertThatThrownBy(() -> payBoletoService.pay(dto))
                .isInstanceOf(InsufficientFundsException.class);
    }
}
