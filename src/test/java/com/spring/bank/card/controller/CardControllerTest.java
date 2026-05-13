package com.spring.bank.card.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.bank.account.model.Account;
import com.spring.bank.account.service.FindAccountService;
import com.spring.bank.auth.security.JwtTokenProvider;
import com.spring.bank.card.dto.CardResponseDTO;
import com.spring.bank.card.dto.CreateCardDTO;
import com.spring.bank.card.dto.PurchaseDTO;
import com.spring.bank.card.enums.CardStatus;
import com.spring.bank.card.enums.CardType;
import com.spring.bank.card.model.Card;
import com.spring.bank.card.service.CardPaymentService;
import com.spring.bank.card.service.CreateCardService;
import com.spring.bank.card.service.FindCardService;
import com.spring.bank.common.exception.CardNotFoundException;
import com.spring.bank.common.exception.InsufficientFundsException;
import com.spring.bank.common.exception.InvalidCvvException;
import com.spring.bank.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CardController.class)
@AutoConfigureMockMvc(addFilters = false)
class CardControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private CreateCardService createCardService;
    @MockBean private FindCardService findCardService;
    @MockBean private CardPaymentService cardPaymentService;
    @MockBean private FindAccountService findAccountService;
    @MockBean private JwtTokenProvider jwtTokenProvider;

    private Card card;
    private Account account;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setName("John");

        account = new Account();
        account.setId(1L);
        account.setUser(user);

        card = new Card();
        card.setId("uuid-123");
        card.setAccount(account);
        card.setCardType(CardType.DEBIT);
        card.setCardStatus(CardStatus.ACTIVE);
        card.setNumber("9900000000000001");
        card.setExpirationDate(LocalDate.now().plusYears(3));
    }

    @Test
    void createCard_shouldReturn201WithCardData() throws Exception {
        when(createCardService.create(1L, CardType.DEBIT)).thenReturn(card);

        mockMvc.perform(post("/card")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateCardDTO(1L, CardType.DEBIT))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("DEBIT"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void listCards_shouldReturn200WithCardList() throws Exception {
        when(findAccountService.getById(1L)).thenReturn(account);
        when(findCardService.listByAccount(account)).thenReturn(List.of(new CardResponseDTO(card)));

        mockMvc.perform(get("/card/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("DEBIT"));
    }

    @Test
    void purchase_shouldReturn200WhenPaymentSucceeds() throws Exception {
        PurchaseDTO dto = new PurchaseDTO("9900000000000001", "123", CardType.DEBIT, new BigDecimal("50.00"));
        doNothing().when(cardPaymentService).processPayment(any());

        mockMvc.perform(post("/card/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(cardPaymentService).processPayment(any());
    }

    @Test
    void purchase_shouldReturn422WhenInsufficientFunds() throws Exception {
        PurchaseDTO dto = new PurchaseDTO("9900000000000001", "123", CardType.DEBIT, new BigDecimal("9999.00"));
        doThrow(new InsufficientFundsException("Insufficient funds")).when(cardPaymentService).processPayment(any());

        mockMvc.perform(post("/card/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void purchase_shouldReturn422WhenCvvIsInvalid() throws Exception {
        PurchaseDTO dto = new PurchaseDTO("9900000000000001", "000", CardType.DEBIT, new BigDecimal("50.00"));
        doThrow(new InvalidCvvException("Invalid CVV")).when(cardPaymentService).processPayment(any());

        mockMvc.perform(post("/card/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void purchase_shouldReturn404WhenCardNotFound() throws Exception {
        PurchaseDTO dto = new PurchaseDTO("0000000000000000", "000", CardType.DEBIT, new BigDecimal("50.00"));
        doThrow(new CardNotFoundException("Card not found")).when(cardPaymentService).processPayment(any());

        mockMvc.perform(post("/card/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }
}
