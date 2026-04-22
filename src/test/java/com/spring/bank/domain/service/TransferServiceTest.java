package com.spring.bank.domain.service;

import com.spring.bank.common.config.messaging.RabbitMQConfig;
import com.spring.bank.common.exception.InsufficientFundsException;
import com.spring.bank.common.exception.InvalidAccountException;
import com.spring.bank.common.exception.InvalidTransferAccountTypeException;
import com.spring.bank.domain.dto.transfer.CreateTransferDTO;
import com.spring.bank.domain.enums.account.AccountStatusEnum;
import com.spring.bank.domain.enums.account.AccountTypeEnum;
import com.spring.bank.domain.enums.transfer.TransferStatusEnum;
import com.spring.bank.domain.model.Account;
import com.spring.bank.domain.model.Transfer;
import com.spring.bank.domain.repository.TransferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock private TransferRepository transferRepository;
    @Mock private AccountService accountService;
    @Mock private RabbitTemplate rabbitTemplate;

    @InjectMocks private TransferService transferService;

    private Account fromAccount;
    private Account toAccount;

    @BeforeEach
    void setUp() {
        fromAccount = new Account();
        fromAccount.setId(1L);
        fromAccount.setType(AccountTypeEnum.CHECKING);
        fromAccount.setStatus(AccountStatusEnum.ACTIVE);
        fromAccount.setBalance(new BigDecimal("1000.00"));

        toAccount = new Account();
        toAccount.setId(2L);
        toAccount.setType(AccountTypeEnum.CHECKING);
        toAccount.setStatus(AccountStatusEnum.ACTIVE);
        toAccount.setBalance(new BigDecimal("200.00"));
    }

    @Test
    void create_shouldQueueTransferAndReturnPending() {
        CreateTransferDTO dto = new CreateTransferDTO(1L, 2L, new BigDecimal("300.00"), "Test transfer");
        when(accountService.getById(1L)).thenReturn(fromAccount);
        when(accountService.getById(2L)).thenReturn(toAccount);
        Transfer saved = new Transfer();
        saved.setId(99L);
        saved.setStatus(TransferStatusEnum.PENDING);
        when(transferRepository.save(any())).thenReturn(saved);

        Transfer result = transferService.create(dto);

        assertThat(result.getStatus()).isEqualTo(TransferStatusEnum.PENDING);
        verify(rabbitTemplate).convertAndSend(eq(RabbitMQConfig.TRANSFER_QUEUE), eq(99L));
        verify(accountService, never()).subtractFunds(any(), any());
        verify(accountService, never()).addFunds(any(), any());
    }

    @Test
    void create_shouldThrowWhenTransferringToSameAccount() {
        CreateTransferDTO dto = new CreateTransferDTO(1L, 1L, new BigDecimal("100.00"), null);
        when(accountService.getById(1L)).thenReturn(fromAccount);

        assertThatThrownBy(() -> transferService.create(dto))
                .isInstanceOf(InvalidTransferAccountTypeException.class)
                .hasMessageContaining("same account");
    }

    @Test
    void create_shouldThrowWhenFromAccountIsNotChecking() {
        fromAccount.setType(AccountTypeEnum.SAVINGS);
        CreateTransferDTO dto = new CreateTransferDTO(1L, 2L, new BigDecimal("100.00"), null);
        when(accountService.getById(1L)).thenReturn(fromAccount);
        when(accountService.getById(2L)).thenReturn(toAccount);

        assertThatThrownBy(() -> transferService.create(dto))
                .isInstanceOf(InvalidTransferAccountTypeException.class);
    }

    @Test
    void create_shouldThrowWhenFromAccountIsBlocked() {
        fromAccount.setStatus(AccountStatusEnum.BLOCKED);
        CreateTransferDTO dto = new CreateTransferDTO(1L, 2L, new BigDecimal("100.00"), null);
        when(accountService.getById(1L)).thenReturn(fromAccount);
        when(accountService.getById(2L)).thenReturn(toAccount);

        assertThatThrownBy(() -> transferService.create(dto))
                .isInstanceOf(InvalidAccountException.class)
                .hasMessageContaining("Source account");
    }

    @Test
    void create_shouldThrowWhenToAccountIsBlocked() {
        toAccount.setStatus(AccountStatusEnum.BLOCKED);
        CreateTransferDTO dto = new CreateTransferDTO(1L, 2L, new BigDecimal("100.00"), null);
        when(accountService.getById(1L)).thenReturn(fromAccount);
        when(accountService.getById(2L)).thenReturn(toAccount);

        assertThatThrownBy(() -> transferService.create(dto))
                .isInstanceOf(InvalidAccountException.class)
                .hasMessageContaining("Destination account");
    }

    @Test
    void create_shouldThrowWhenInsufficientFunds() {
        CreateTransferDTO dto = new CreateTransferDTO(1L, 2L, new BigDecimal("9999.00"), null);
        when(accountService.getById(1L)).thenReturn(fromAccount);
        when(accountService.getById(2L)).thenReturn(toAccount);

        assertThatThrownBy(() -> transferService.create(dto))
                .isInstanceOf(InsufficientFundsException.class);

        verify(transferRepository, never()).save(any());
        verify(rabbitTemplate, never()).convertAndSend(any(String.class), any(Long.class));
    }
}
