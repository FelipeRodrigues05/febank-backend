package com.spring.bank.card.service;

import com.spring.bank.account.enums.AccountStatusEnum;
import com.spring.bank.account.enums.AccountTypeEnum;
import com.spring.bank.account.model.Account;
import com.spring.bank.account.service.AccountFundsService;
import com.spring.bank.card.dto.PurchaseDTO;
import com.spring.bank.card.enums.CardStatus;
import com.spring.bank.card.enums.CardType;
import com.spring.bank.card.model.Card;
import com.spring.bank.card.repository.CardRepository;
import com.spring.bank.common.exception.*;
import com.spring.bank.transaction.service.CreateTransactionService;
import com.spring.bank.user.model.User;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardPaymentServiceTest {

    @Mock private CardRepository cardRepository;
    @Mock private AccountFundsService accountFundsService;
    @Mock private CreateTransactionService createTransactionService;
    @Mock private AddBillPurchaseService addBillPurchaseService;
    @Mock private FindCardService findCardService;

    @InjectMocks private CardPaymentService cardPaymentService;

    private Account account;
    private Card debitCard;
    private Card creditCard;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId(1L);
        user.setName("Jane");

        account = new Account();
        account.setId(1L);
        account.setUser(user);
        account.setType(AccountTypeEnum.CHECKING);
        account.setStatus(AccountStatusEnum.ACTIVE);
        account.setBalance(new BigDecimal("500.00"));

        debitCard = new Card();
        debitCard.setId("debit-card-uuid");
        debitCard.setAccount(account);
        debitCard.setCardType(CardType.DEBIT);
        debitCard.setCardStatus(CardStatus.ACTIVE);
        debitCard.setCvv("123");
        debitCard.setNumber("9900000000000001");
        debitCard.setExpirationDate(LocalDate.now().plusYears(3));

        creditCard = new Card();
        creditCard.setId("credit-card-uuid");
        creditCard.setAccount(account);
        creditCard.setCardType(CardType.CREDIT);
        creditCard.setCardStatus(CardStatus.ACTIVE);
        creditCard.setCvv("456");
        creditCard.setNumber("9900000000000002");
        creditCard.setExpirationDate(LocalDate.now().plusYears(3));
        creditCard.setLimitAvailable(new BigDecimal("1000.00"));
        creditCard.setUsedLimit(BigDecimal.ZERO);
    }

    @Test
    void processPayment_debit_shouldSubtractFromAccountAndCreateTransaction() {
        PurchaseDTO dto = new PurchaseDTO("9900000000000001", "123", CardType.DEBIT, new BigDecimal("100.00"));
        when(findCardService.validateForPayment("9900000000000001", "123", CardType.DEBIT)).thenReturn(debitCard);

        cardPaymentService.processPayment(dto);

        verify(accountFundsService).subtractFunds(account.getId(), new BigDecimal("100.00"));
        verify(createTransactionService).create(any());
    }

    @Test
    void processPayment_debit_shouldThrowWhenInsufficientFunds() {
        account.setBalance(new BigDecimal("50.00"));
        PurchaseDTO dto = new PurchaseDTO("9900000000000001", "123", CardType.DEBIT, new BigDecimal("9999.00"));
        when(findCardService.validateForPayment("9900000000000001", "123", CardType.DEBIT)).thenReturn(debitCard);

        assertThatThrownBy(() -> cardPaymentService.processPayment(dto))
                .isInstanceOf(InsufficientFundsException.class);

        verify(accountFundsService, never()).subtractFunds(any(), any());
    }

    @Test
    void processPayment_credit_shouldDecreaseLimitAndAddBillPurchase() {
        PurchaseDTO dto = new PurchaseDTO("9900000000000002", "456", CardType.CREDIT, new BigDecimal("300.00"));
        when(findCardService.validateForPayment("9900000000000002", "456", CardType.CREDIT)).thenReturn(creditCard);
        when(cardRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        cardPaymentService.processPayment(dto);

        assertThat(creditCard.getLimitAvailable()).isEqualByComparingTo(new BigDecimal("700.00"));
        assertThat(creditCard.getUsedLimit()).isEqualByComparingTo(new BigDecimal("300.00"));
        verify(accountFundsService, never()).subtractFunds(any(), any());
        verify(addBillPurchaseService).addPurchase(creditCard, new BigDecimal("300.00"));
    }

    @Test
    void processPayment_credit_shouldThrowWhenLimitInsufficient() {
        PurchaseDTO dto = new PurchaseDTO("9900000000000002", "456", CardType.CREDIT, new BigDecimal("2000.00"));
        when(findCardService.validateForPayment("9900000000000002", "456", CardType.CREDIT)).thenReturn(creditCard);

        assertThatThrownBy(() -> cardPaymentService.processPayment(dto))
                .isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    void processPayment_shouldThrowWhenCardIsBlocked() {
        PurchaseDTO dto = new PurchaseDTO("9900000000000001", "123", CardType.DEBIT, new BigDecimal("100.00"));
        when(findCardService.validateForPayment("9900000000000001", "123", CardType.DEBIT))
                .thenThrow(new CardNotActiveException("Card is not active."));

        assertThatThrownBy(() -> cardPaymentService.processPayment(dto))
                .isInstanceOf(CardNotActiveException.class);
    }

    @Test
    void processPayment_shouldThrowWhenCardIsExpired() {
        PurchaseDTO dto = new PurchaseDTO("9900000000000001", "123", CardType.DEBIT, new BigDecimal("100.00"));
        when(findCardService.validateForPayment("9900000000000001", "123", CardType.DEBIT))
                .thenThrow(new ExpiredCardException("Card is expired"));

        assertThatThrownBy(() -> cardPaymentService.processPayment(dto))
                .isInstanceOf(ExpiredCardException.class);
    }

    @Test
    void processPayment_shouldThrowWhenCvvIsInvalid() {
        PurchaseDTO dto = new PurchaseDTO("9900000000000001", "999", CardType.DEBIT, new BigDecimal("100.00"));
        when(findCardService.validateForPayment("9900000000000001", "999", CardType.DEBIT))
                .thenThrow(new InvalidCvvException("Invalid CVV"));

        assertThatThrownBy(() -> cardPaymentService.processPayment(dto))
                .isInstanceOf(InvalidCvvException.class);
    }

    @Test
    void processPayment_shouldThrowWhenAccountIsNotActive() {
        account.setStatus(AccountStatusEnum.BLOCKED);
        PurchaseDTO dto = new PurchaseDTO("9900000000000001", "123", CardType.DEBIT, new BigDecimal("100.00"));
        when(findCardService.validateForPayment("9900000000000001", "123", CardType.DEBIT)).thenReturn(debitCard);

        assertThatThrownBy(() -> cardPaymentService.processPayment(dto))
                .isInstanceOf(InvalidAccountException.class);
    }

    @Test
    void processPayment_shouldThrowWhenCardNotFound() {
        PurchaseDTO dto = new PurchaseDTO("0000000000000000", "123", CardType.DEBIT, new BigDecimal("100.00"));
        when(findCardService.validateForPayment("0000000000000000", "123", CardType.DEBIT))
                .thenThrow(new CardNotFoundException("Card not found"));

        assertThatThrownBy(() -> cardPaymentService.processPayment(dto))
                .isInstanceOf(CardNotFoundException.class);
    }
}
