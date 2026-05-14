package com.spring.bank.domain.service;

import com.spring.bank.common.exception.*;
import com.spring.bank.domain.service.CardBillService;
import com.spring.bank.domain.dto.card.PurchaseDTO;
import com.spring.bank.domain.enums.account.AccountStatusEnum;
import com.spring.bank.domain.enums.account.AccountTypeEnum;
import com.spring.bank.domain.enums.card.CardStatus;
import com.spring.bank.domain.enums.card.CardType;
import com.spring.bank.domain.model.Account;
import com.spring.bank.domain.model.Card;
import com.spring.bank.domain.model.User;
import com.spring.bank.domain.repository.CardRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock private CardRepository cardRepository;
    @Mock private AccountService accountService;
    @Mock private TransactionService transactionService;
    @Mock private CardBillService cardBillService;

    @InjectMocks private CardService cardService;

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
    void processPayment_debit_shouldSubtractFromAccountBalance() {
        PurchaseDTO dto = new PurchaseDTO("9900000000000001", "123", new BigDecimal("100.00"), CardType.DEBIT);
        when(cardRepository.findByNumber("9900000000000001")).thenReturn(Optional.of(debitCard));

        cardService.processPayment(dto);

        verify(accountService).subtractFunds(account.getId(), new BigDecimal("100.00"));
        verify(transactionService).create(any());
    }

    @Test
    void processPayment_debit_shouldThrowWhenInsufficientFunds() {
        PurchaseDTO dto = new PurchaseDTO("9900000000000001", "123", new BigDecimal("9999.00"), CardType.DEBIT);
        when(cardRepository.findByNumber("9900000000000001")).thenReturn(Optional.of(debitCard));

        assertThatThrownBy(() -> cardService.processPayment(dto))
                .isInstanceOf(InsufficientFundsException.class);

        verify(accountService, never()).subtractFunds(any(), any());
    }

    @Test
    void processPayment_credit_shouldDecreaseLimitAndIncreaseUsedLimit() {
        PurchaseDTO dto = new PurchaseDTO("9900000000000002", "456", new BigDecimal("300.00"), CardType.CREDIT);
        when(cardRepository.findByNumber("9900000000000002")).thenReturn(Optional.of(creditCard));
        when(cardRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        cardService.processPayment(dto);

        assertThat(creditCard.getLimitAvailable()).isEqualByComparingTo(new BigDecimal("700.00"));
        assertThat(creditCard.getUsedLimit()).isEqualByComparingTo(new BigDecimal("300.00"));
        verify(accountService, never()).subtractFunds(any(), any());
    }

    @Test
    void processPayment_credit_shouldThrowWhenLimitInsufficient() {
        PurchaseDTO dto = new PurchaseDTO("9900000000000002", "456", new BigDecimal("2000.00"), CardType.CREDIT);
        when(cardRepository.findByNumber("9900000000000002")).thenReturn(Optional.of(creditCard));

        assertThatThrownBy(() -> cardService.processPayment(dto))
                .isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    void processPayment_shouldThrowWhenCardIsBlocked() {
        debitCard.setCardStatus(CardStatus.BLOCKED);
        PurchaseDTO dto = new PurchaseDTO("9900000000000001", "123", new BigDecimal("100.00"), CardType.DEBIT);
        when(cardRepository.findByNumber("9900000000000001")).thenReturn(Optional.of(debitCard));

        assertThatThrownBy(() -> cardService.processPayment(dto))
                .isInstanceOf(CardNotActiveException.class);
    }

    @Test
    void processPayment_shouldThrowWhenCardIsExpired() {
        debitCard.setExpirationDate(LocalDate.now().minusDays(1));
        PurchaseDTO dto = new PurchaseDTO("9900000000000001", "123", new BigDecimal("100.00"), CardType.DEBIT);
        when(cardRepository.findByNumber("9900000000000001")).thenReturn(Optional.of(debitCard));

        assertThatThrownBy(() -> cardService.processPayment(dto))
                .isInstanceOf(ExpiredCardException.class);
    }

    @Test
    void processPayment_shouldThrowWhenCvvIsInvalid() {
        PurchaseDTO dto = new PurchaseDTO("9900000000000001", "999", new BigDecimal("100.00"), CardType.DEBIT);
        when(cardRepository.findByNumber("9900000000000001")).thenReturn(Optional.of(debitCard));

        assertThatThrownBy(() -> cardService.processPayment(dto))
                .isInstanceOf(InvalidCvvException.class);
    }

    @Test
    void processPayment_shouldThrowWhenCardTypeDoesNotMatch() {
        PurchaseDTO dto = new PurchaseDTO("9900000000000001", "123", new BigDecimal("100.00"), CardType.CREDIT);
        when(cardRepository.findByNumber("9900000000000001")).thenReturn(Optional.of(debitCard));

        assertThatThrownBy(() -> cardService.processPayment(dto))
                .isInstanceOf(InvalidCardTypeException.class);
    }

    @Test
    void processPayment_shouldThrowWhenAccountIsNotActive() {
        account.setStatus(AccountStatusEnum.BLOCKED);
        PurchaseDTO dto = new PurchaseDTO("9900000000000001", "123", new BigDecimal("100.00"), CardType.DEBIT);
        when(cardRepository.findByNumber("9900000000000001")).thenReturn(Optional.of(debitCard));

        assertThatThrownBy(() -> cardService.processPayment(dto))
                .isInstanceOf(InvalidAccountException.class);
    }

    @Test
    void processPayment_shouldThrowWhenCardNotFound() {
        PurchaseDTO dto = new PurchaseDTO("0000000000000000", "123", new BigDecimal("100.00"), CardType.DEBIT);
        when(cardRepository.findByNumber("0000000000000000")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.processPayment(dto))
                .isInstanceOf(CardNotFoundException.class);
    }
}
