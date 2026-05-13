package com.spring.bank.card.service;

import com.spring.bank.account.model.Account;
import com.spring.bank.account.service.AccountFundsService;
import com.spring.bank.card.model.Card;
import com.spring.bank.card.model.CashbackRecord;
import com.spring.bank.card.repository.CashbackRepository;
import com.spring.bank.transaction.service.CreateTransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CashbackServiceTest {

    @Mock private CashbackRepository cashbackRepository;
    @Mock private FindCardByIdService findCardByIdService;
    @Mock private AccountFundsService accountFundsService;
    @Mock private CreateTransactionService createTransactionService;

    @InjectMocks private CashbackService cashbackService;

    private Card card;
    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setId(1L);

        card = new Card();
        card.setId("card-001");
        card.setAccount(account);
    }

    @Test
    void recordCashback_shouldSaveCashbackRecord() {
        BigDecimal purchaseAmount = new BigDecimal("200.00");

        cashbackService.recordCashback(card, purchaseAmount);

        ArgumentCaptor<CashbackRecord> captor = ArgumentCaptor.forClass(CashbackRecord.class);
        verify(cashbackRepository).save(captor.capture());

        CashbackRecord savedRecord = captor.getValue();
        assertThat(savedRecord.getCashbackAmount()).isEqualByComparingTo(new BigDecimal("1.00"));
        assertThat(savedRecord.getTransactionAmount()).isEqualByComparingTo(purchaseAmount);
        assertThat(savedRecord.isCredited()).isFalse();
    }

    @Test
    void creditPending_shouldCreditFundsForUncreditedRecords() {
        CashbackRecord firstRecord = new CashbackRecord();
        firstRecord.setCashbackAmount(new BigDecimal("1.00"));
        firstRecord.setCredited(false);

        CashbackRecord secondRecord = new CashbackRecord();
        secondRecord.setCashbackAmount(new BigDecimal("0.50"));
        secondRecord.setCredited(false);

        when(findCardByIdService.getById("card-001")).thenReturn(card);
        when(cashbackRepository.findByCardIdAndCreditedFalse("card-001")).thenReturn(List.of(firstRecord, secondRecord));

        BigDecimal creditedTotal = cashbackService.creditPending("card-001");

        assertThat(creditedTotal).isEqualByComparingTo(new BigDecimal("1.50"));
        verify(accountFundsService).addFunds(1L, new BigDecimal("1.50"));
        verify(createTransactionService).create(any());
        assertThat(firstRecord.isCredited()).isTrue();
        assertThat(secondRecord.isCredited()).isTrue();
    }
}
