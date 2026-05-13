package com.spring.bank.transfer.service;

import com.spring.bank.account.enums.AccountStatusEnum;
import com.spring.bank.account.enums.AccountTypeEnum;
import com.spring.bank.account.model.Account;
import com.spring.bank.account.service.FindAccountService;
import com.spring.bank.common.exception.InsufficientFundsException;
import com.spring.bank.common.exception.InvalidAccountException;
import com.spring.bank.common.exception.InvalidTransferAccountTypeException;
import com.spring.bank.transfer.dto.CreateTransferDTO;
import com.spring.bank.transfer.enums.TransferStatusEnum;
import com.spring.bank.transfer.messaging.TransferPublisher;
import com.spring.bank.transfer.model.Transfer;
import com.spring.bank.transfer.repository.TransferRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateTransferServiceTest {

    @Mock private TransferRepository transferRepository;
    @Mock private FindAccountService findAccountService;
    @Mock private TransferPublisher transferPublisher;

    @InjectMocks private CreateTransferService createTransferService;

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
        when(findAccountService.getById(1L)).thenReturn(fromAccount);
        when(findAccountService.getById(2L)).thenReturn(toAccount);

        Transfer saved = new Transfer();
        saved.setId(99L);
        saved.setStatus(TransferStatusEnum.PENDING);
        when(transferRepository.save(any())).thenReturn(saved);

        Transfer result = createTransferService.create(dto);

        assertThat(result.getStatus()).isEqualTo(TransferStatusEnum.PENDING);
        verify(transferPublisher).publish(99L);
    }

    @Test
    void create_shouldThrowWhenTransferringToSameAccount() {
        CreateTransferDTO dto = new CreateTransferDTO(1L, 1L, new BigDecimal("100.00"), null);
        when(findAccountService.getById(1L)).thenReturn(fromAccount);

        assertThatThrownBy(() -> createTransferService.create(dto))
                .isInstanceOf(InvalidTransferAccountTypeException.class)
                .hasMessageContaining("same account");
    }

    @Test
    void create_shouldThrowWhenFromAccountIsNotChecking() {
        fromAccount.setType(AccountTypeEnum.SAVINGS);
        CreateTransferDTO dto = new CreateTransferDTO(1L, 2L, new BigDecimal("100.00"), null);
        when(findAccountService.getById(1L)).thenReturn(fromAccount);
        when(findAccountService.getById(2L)).thenReturn(toAccount);

        assertThatThrownBy(() -> createTransferService.create(dto))
                .isInstanceOf(InvalidTransferAccountTypeException.class);
    }

    @Test
    void create_shouldThrowWhenFromAccountIsBlocked() {
        fromAccount.setStatus(AccountStatusEnum.BLOCKED);
        CreateTransferDTO dto = new CreateTransferDTO(1L, 2L, new BigDecimal("100.00"), null);
        when(findAccountService.getById(1L)).thenReturn(fromAccount);
        when(findAccountService.getById(2L)).thenReturn(toAccount);

        assertThatThrownBy(() -> createTransferService.create(dto))
                .isInstanceOf(InvalidAccountException.class)
                .hasMessageContaining("Source account");
    }

    @Test
    void create_shouldThrowWhenToAccountIsBlocked() {
        toAccount.setStatus(AccountStatusEnum.BLOCKED);
        CreateTransferDTO dto = new CreateTransferDTO(1L, 2L, new BigDecimal("100.00"), null);
        when(findAccountService.getById(1L)).thenReturn(fromAccount);
        when(findAccountService.getById(2L)).thenReturn(toAccount);

        assertThatThrownBy(() -> createTransferService.create(dto))
                .isInstanceOf(InvalidAccountException.class)
                .hasMessageContaining("Destination account");
    }

    @Test
    void create_shouldThrowWhenInsufficientFunds() {
        CreateTransferDTO dto = new CreateTransferDTO(1L, 2L, new BigDecimal("9999.00"), null);
        when(findAccountService.getById(1L)).thenReturn(fromAccount);
        when(findAccountService.getById(2L)).thenReturn(toAccount);

        assertThatThrownBy(() -> createTransferService.create(dto))
                .isInstanceOf(InsufficientFundsException.class);

        verify(transferRepository, never()).save(any());
        verify(transferPublisher, never()).publish(any());
    }
}
