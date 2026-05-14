package com.spring.bank.card.service;

import com.spring.bank.card.dto.RequestLimitIncreaseDTO;
import com.spring.bank.card.enums.CardType;
import com.spring.bank.card.enums.LimitIncreaseStatus;
import com.spring.bank.card.model.Card;
import com.spring.bank.card.model.LimitIncreaseRequest;
import com.spring.bank.card.repository.CardRepository;
import com.spring.bank.card.repository.LimitIncreaseRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LimitIncreaseServiceTest {

    @Mock private LimitIncreaseRequestRepository limitIncreaseRequestRepository;
    @Mock private FindCardByIdService findCardByIdService;
    @Mock private CardRepository cardRepository;

    @InjectMocks private LimitIncreaseService limitIncreaseService;

    private Card creditCard;

    @BeforeEach
    void setUp() {
        creditCard = new Card();
        creditCard.setId("card-001");
        creditCard.setCardType(CardType.CREDIT);
        creditCard.setLimitAvailable(new BigDecimal("1000.00"));
    }

    @Test
    void request_shouldAutoApproveWhenWithinTripleCurrentLimit() {
        RequestLimitIncreaseDTO dto = new RequestLimitIncreaseDTO("card-001", new BigDecimal("3000.00"));

        LimitIncreaseRequest savedRequest = new LimitIncreaseRequest();
        savedRequest.setStatus(LimitIncreaseStatus.APPROVED);
        savedRequest.setRequestedLimit(new BigDecimal("3000.00"));

        when(findCardByIdService.getById("card-001")).thenReturn(creditCard);
        when(limitIncreaseRequestRepository.save(any())).thenReturn(savedRequest);

        LimitIncreaseRequest result = limitIncreaseService.request(dto);

        assertThat(result.getStatus()).isEqualTo(LimitIncreaseStatus.APPROVED);
        verify(cardRepository).save(creditCard);
    }

    @Test
    void request_shouldRejectWhenExceedsTripleCurrentLimit() {
        RequestLimitIncreaseDTO dto = new RequestLimitIncreaseDTO("card-001", new BigDecimal("3001.00"));

        LimitIncreaseRequest savedRequest = new LimitIncreaseRequest();
        savedRequest.setStatus(LimitIncreaseStatus.PENDING);
        savedRequest.setRequestedLimit(new BigDecimal("3001.00"));

        when(findCardByIdService.getById("card-001")).thenReturn(creditCard);
        when(limitIncreaseRequestRepository.save(any())).thenReturn(savedRequest);

        LimitIncreaseRequest result = limitIncreaseService.request(dto);

        assertThat(result.getStatus()).isEqualTo(LimitIncreaseStatus.PENDING);
    }
}
