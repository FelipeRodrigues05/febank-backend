package com.spring.bank.account.service;

import com.spring.bank.account.dto.OpenAccountDTO;
import com.spring.bank.account.enums.AccountStatusEnum;
import com.spring.bank.account.enums.AccountTypeEnum;
import com.spring.bank.account.model.Account;
import com.spring.bank.account.repository.AccountRepository;
import com.spring.bank.common.exception.AccountTypeAlreadyExistsException;
import com.spring.bank.common.utils.AccountNumberGenerator;
import com.spring.bank.user.model.User;
import com.spring.bank.user.service.FindUserService;
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
class OpenAccountServiceTest {

    @Mock private AccountRepository accountRepository;
    @Mock private AccountNumberGenerator accountNumberGenerator;
    @Mock private FindUserService findUserService;

    @InjectMocks private OpenAccountService openAccountService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John Doe");
    }

    @Test
    void open_shouldCreateAccountWithZeroBalanceAndActiveStatus() {
        OpenAccountDTO dto = new OpenAccountDTO(1L, AccountTypeEnum.CHECKING);
        when(findUserService.getById(1L)).thenReturn(user);
        when(accountRepository.existsByUserIdAndType(1L, AccountTypeEnum.CHECKING)).thenReturn(false);
        when(accountNumberGenerator.generateAccount()).thenReturn("10000001");
        when(accountRepository.existsByNumber("10000001")).thenReturn(false);
        when(accountRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Account result = openAccountService.open(dto);

        assertThat(result.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getStatus()).isEqualTo(AccountStatusEnum.ACTIVE);
        assertThat(result.getType()).isEqualTo(AccountTypeEnum.CHECKING);
    }

    @Test
    void open_shouldRetryUntilUniqueAccountNumberIsFound() {
        OpenAccountDTO dto = new OpenAccountDTO(1L, AccountTypeEnum.CHECKING);
        when(findUserService.getById(1L)).thenReturn(user);
        when(accountRepository.existsByUserIdAndType(1L, AccountTypeEnum.CHECKING)).thenReturn(false);
        when(accountNumberGenerator.generateAccount()).thenReturn("DUPLICATE", "UNIQUE");
        when(accountRepository.existsByNumber("DUPLICATE")).thenReturn(true);
        when(accountRepository.existsByNumber("UNIQUE")).thenReturn(false);
        when(accountRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Account result = openAccountService.open(dto);

        assertThat(result.getNumber()).isEqualTo("UNIQUE");
        verify(accountNumberGenerator, times(2)).generateAccount();
    }

    @Test
    void open_shouldThrowWhenUserAlreadyHasThatAccountType() {
        OpenAccountDTO dto = new OpenAccountDTO(1L, AccountTypeEnum.CHECKING);
        when(findUserService.getById(1L)).thenReturn(user);
        when(accountRepository.existsByUserIdAndType(1L, AccountTypeEnum.CHECKING)).thenReturn(true);

        assertThatThrownBy(() -> openAccountService.open(dto))
                .isInstanceOf(AccountTypeAlreadyExistsException.class)
                .hasMessageContaining("CHECKING");
    }
}
