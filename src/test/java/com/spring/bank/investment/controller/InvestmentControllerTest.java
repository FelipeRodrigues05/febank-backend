package com.spring.bank.investment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.bank.account.enums.AccountStatusEnum;
import com.spring.bank.account.enums.AccountTypeEnum;
import com.spring.bank.account.model.Account;
import com.spring.bank.auth.security.JwtTokenProvider;
import com.spring.bank.common.exception.InsufficientFundsException;
import com.spring.bank.investment.dto.ApplyInvestmentDTO;
import com.spring.bank.investment.dto.RedeemInvestmentDTO;
import com.spring.bank.investment.service.ApplyInvestmentService;
import com.spring.bank.investment.service.RedeemInvestmentService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InvestmentController.class)
@AutoConfigureMockMvc(addFilters = false)
class InvestmentControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private ApplyInvestmentService applyInvestmentService;
    @MockBean private RedeemInvestmentService redeemInvestmentService;
    @MockBean private JwtTokenProvider jwtTokenProvider;

    private Account investmentAccount;

    @BeforeEach
    void setUp() {
        investmentAccount = new Account();
        investmentAccount.setId(2L);
        investmentAccount.setNumber("20000002");
        investmentAccount.setType(AccountTypeEnum.INVESTMENT);
        investmentAccount.setStatus(AccountStatusEnum.ACTIVE);
        investmentAccount.setBalance(new BigDecimal("1500.00"));
    }

    @Test
    void apply_shouldReturn201WithInvestmentAccountData() throws Exception {
        ApplyInvestmentDTO dto = new ApplyInvestmentDTO(1L, new BigDecimal("500.00"));
        when(applyInvestmentService.apply(any())).thenReturn(investmentAccount);

        mockMvc.perform(post("/investment/apply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountId").value(2))
                .andExpect(jsonPath("$.balance").value(1500.00));
    }

    @Test
    void apply_shouldReturn422WhenInsufficientFunds() throws Exception {
        ApplyInvestmentDTO dto = new ApplyInvestmentDTO(1L, new BigDecimal("99999.00"));
        when(applyInvestmentService.apply(any())).thenThrow(new InsufficientFundsException("Insufficient balance"));

        mockMvc.perform(post("/investment/apply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void redeem_shouldReturn200WithUpdatedBalance() throws Exception {
        investmentAccount.setBalance(new BigDecimal("1000.00"));
        RedeemInvestmentDTO dto = new RedeemInvestmentDTO(1L, new BigDecimal("500.00"));
        when(redeemInvestmentService.redeem(any())).thenReturn(investmentAccount);

        mockMvc.perform(post("/investment/redeem")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1000.00));
    }

    @Test
    void redeem_shouldReturn422WhenInvestmentBalanceInsufficient() throws Exception {
        RedeemInvestmentDTO dto = new RedeemInvestmentDTO(1L, new BigDecimal("99999.00"));
        when(redeemInvestmentService.redeem(any())).thenThrow(new InsufficientFundsException("Insufficient balance"));

        mockMvc.perform(post("/investment/redeem")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity());
    }
}
