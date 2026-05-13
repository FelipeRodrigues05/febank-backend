package com.spring.bank.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.bank.account.dto.DepositDTO;
import com.spring.bank.account.dto.OpenAccountDTO;
import com.spring.bank.account.dto.WithdrawDTO;
import com.spring.bank.account.enums.AccountStatusEnum;
import com.spring.bank.account.enums.AccountTypeEnum;
import com.spring.bank.account.model.Account;
import com.spring.bank.account.service.AccountMovementService;
import com.spring.bank.account.service.FindAccountService;
import com.spring.bank.account.service.OpenAccountService;
import com.spring.bank.auth.security.JwtTokenProvider;
import com.spring.bank.common.exception.AccountTypeAlreadyExistsException;
import com.spring.bank.common.exception.InsufficientFundsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
@AutoConfigureMockMvc(addFilters = false)
class AccountControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private OpenAccountService openAccountService;
    @MockBean private FindAccountService findAccountService;
    @MockBean private AccountMovementService accountMovementService;
    @MockBean private JwtTokenProvider jwtTokenProvider;

    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setId(1L);
        account.setNumber("10000001");
        account.setType(AccountTypeEnum.CHECKING);
        account.setStatus(AccountStatusEnum.ACTIVE);
        account.setBalance(new BigDecimal("0.00"));
    }

    @Test
    void createAccount_shouldReturn201WithAccountData() throws Exception {
        OpenAccountDTO dto = new OpenAccountDTO(1L, AccountTypeEnum.CHECKING);
        when(openAccountService.open(any())).thenReturn(account);

        mockMvc.perform(post("/account/open")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.number").value("10000001"))
                .andExpect(jsonPath("$.type").value("CHECKING"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void createAccount_shouldReturn409WhenDuplicateAccountType() throws Exception {
        OpenAccountDTO dto = new OpenAccountDTO(1L, AccountTypeEnum.CHECKING);
        when(openAccountService.open(any())).thenThrow(new AccountTypeAlreadyExistsException("User already has a CHECKING account"));

        mockMvc.perform(post("/account/open")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    void getAccount_shouldReturn200WithAccountData() throws Exception {
        when(findAccountService.getByNumber("10000001")).thenReturn(account);

        mockMvc.perform(get("/account/10000001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value("10000001"));
    }

    @Test
    void deposit_shouldReturn200WithUpdatedBalance() throws Exception {
        account.setBalance(new BigDecimal("100.00"));
        DepositDTO dto = new DepositDTO(1L, new BigDecimal("100.00"));
        when(accountMovementService.deposit(any())).thenReturn(account);

        mockMvc.perform(post("/account/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(100.00));
    }

    @Test
    void deposit_shouldReturn422WhenInsufficientFunds() throws Exception {
        DepositDTO dto = new DepositDTO(1L, new BigDecimal("99999.00"));
        when(accountMovementService.deposit(any())).thenThrow(new InsufficientFundsException("Insufficient balance"));

        mockMvc.perform(post("/account/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void withdraw_shouldReturn200OnSuccess() throws Exception {
        WithdrawDTO dto = new WithdrawDTO(1L, new BigDecimal("50.00"));
        when(accountMovementService.withdraw(any())).thenReturn(account);

        mockMvc.perform(post("/account/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }
}
