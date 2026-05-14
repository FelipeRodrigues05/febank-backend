package com.spring.bank.boleto.service;

import com.spring.bank.account.enums.AccountStatusEnum;
import com.spring.bank.account.model.Account;
import com.spring.bank.account.service.FindAccountService;
import com.spring.bank.boleto.dto.EmitBoletoDTO;
import com.spring.bank.boleto.enums.BoletoStatus;
import com.spring.bank.boleto.model.Boleto;
import com.spring.bank.boleto.repository.BoletoRepository;
import com.spring.bank.boleto.utils.BoletoCodeGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmitBoletoServiceTest {

    @Mock private BoletoRepository boletoRepository;
    @Mock private FindAccountService findAccountService;
    @Mock private BoletoCodeGenerator boletoCodeGenerator;

    @InjectMocks private EmitBoletoService emitBoletoService;

    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setId(1L);
        account.setStatus(AccountStatusEnum.ACTIVE);
    }

    @Test
    void emit_shouldSaveBoletoWithGeneratedCode() {
        String generatedCode = "34191.75000 00000.000000 00000.000000 0 10000000000100";
        EmitBoletoDTO dto = new EmitBoletoDTO(1L, "123.456.789-00", new BigDecimal("100.00"), "Test boleto", LocalDate.now().plusDays(5));

        when(findAccountService.getById(1L)).thenReturn(account);
        when(boletoCodeGenerator.generate(any(), any())).thenReturn(generatedCode);
        when(boletoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Boleto result = emitBoletoService.emit(dto);

        assertThat(result.getStatus()).isEqualTo(BoletoStatus.PENDING);
        assertThat(result.getBoletoCode()).isEqualTo(generatedCode);
    }

    @Test
    void emit_shouldThrowWhenAccountNotActive() {
        account.setStatus(AccountStatusEnum.BLOCKED);
        EmitBoletoDTO dto = new EmitBoletoDTO(1L, "123.456.789-00", new BigDecimal("100.00"), "Test boleto", LocalDate.now().plusDays(5));

        when(findAccountService.getById(1L)).thenReturn(account);

        assertThatThrownBy(() -> emitBoletoService.emit(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not active");
    }
}
