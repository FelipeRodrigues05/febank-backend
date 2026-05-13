package com.spring.bank.card.service;

import com.spring.bank.card.dto.LimitIncreaseResponseDTO;
import com.spring.bank.card.dto.RequestLimitIncreaseDTO;
import com.spring.bank.card.enums.CardType;
import com.spring.bank.card.enums.LimitIncreaseStatus;
import com.spring.bank.card.model.Card;
import com.spring.bank.card.model.LimitIncreaseRequest;
import com.spring.bank.card.repository.CardRepository;
import com.spring.bank.card.repository.LimitIncreaseRequestRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LimitIncreaseService {

    private static final Logger log = LoggerFactory.getLogger(LimitIncreaseService.class);
    private static final BigDecimal AUTO_APPROVE_MULTIPLIER = new BigDecimal("3");

    private final LimitIncreaseRequestRepository limitIncreaseRequestRepository;
    private final FindCardByIdService findCardByIdService;
    private final CardRepository cardRepository;

    @Transactional
    public LimitIncreaseRequest request(RequestLimitIncreaseDTO dto) {
        Card card = findCardByIdService.getById(dto.cardId());

        if (card.getCardType() != CardType.CREDIT) {
            throw new IllegalArgumentException("Limit increase is only available for credit cards");
        }

        BigDecimal currentLimit = card.getLimitAvailable();
        BigDecimal autoApproveThreshold = currentLimit.multiply(AUTO_APPROVE_MULTIPLIER);
        LimitIncreaseStatus status = dto.requestedLimit().compareTo(autoApproveThreshold) <= 0
                ? LimitIncreaseStatus.APPROVED
                : LimitIncreaseStatus.PENDING;

        LimitIncreaseRequest limitRequest = new LimitIncreaseRequest();
        limitRequest.setCard(card);
        limitRequest.setRequestedLimit(dto.requestedLimit());
        limitRequest.setCurrentLimit(currentLimit);
        limitRequest.setStatus(status);

        if (status == LimitIncreaseStatus.APPROVED) {
            card.setLimitAvailable(dto.requestedLimit());
            cardRepository.save(card);
        }

        LimitIncreaseRequest saved = limitIncreaseRequestRepository.save(limitRequest);
        log.info("Limit increase request: id={} cardId={} status={} requested={}", saved.getId(), dto.cardId(), status, dto.requestedLimit());
        return saved;
    }

    public List<LimitIncreaseResponseDTO> listByCard(String cardId) {
        return limitIncreaseRequestRepository.findByCardId(cardId)
                .stream()
                .map(LimitIncreaseResponseDTO::new)
                .toList();
    }
}
