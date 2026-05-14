package com.spring.bank.card.service;

import com.spring.bank.account.service.AccountFundsService;
import com.spring.bank.card.dto.CreateChargebackDTO;
import com.spring.bank.card.enums.ChargebackStatus;
import com.spring.bank.card.model.Card;
import com.spring.bank.card.model.Chargeback;
import com.spring.bank.card.repository.ChargebackRepository;
import com.spring.bank.common.exception.CardNotFoundException;
import com.spring.bank.transaction.dto.CreateTransactionDTO;
import com.spring.bank.transaction.enums.TransactionTypeEnum;
import com.spring.bank.transaction.service.CreateTransactionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChargebackService {

    private static final Logger log = LoggerFactory.getLogger(ChargebackService.class);

    private final ChargebackRepository chargebackRepository;
    private final FindCardByIdService findCardByIdService;
    private final AccountFundsService accountFundsService;
    private final CreateTransactionService createTransactionService;

    public Chargeback request(CreateChargebackDTO dto) {
        Card card = findCardByIdService.getById(dto.cardId());

        Chargeback chargeback = new Chargeback();
        chargeback.setCard(card);
        chargeback.setOriginalTransactionId(dto.transactionId());
        chargeback.setAmount(dto.amount());
        chargeback.setReason(dto.reason());
        chargeback.setStatus(ChargebackStatus.PENDING);

        Chargeback saved = chargebackRepository.save(chargeback);
        log.info("Chargeback requested: id={} cardId={} amount={}", saved.getId(), dto.cardId(), dto.amount());
        return saved;
    }

    @Transactional
    public Chargeback approve(Long chargebackId) {
        Chargeback chargeback = chargebackRepository.findById(chargebackId).orElseThrow(() ->
                new CardNotFoundException("Chargeback " + chargebackId + " not found"));

        chargeback.setStatus(ChargebackStatus.APPROVED);
        chargeback.setReviewedAt(LocalDateTime.now());

        accountFundsService.addFunds(chargeback.getCard().getAccount().getId(), chargeback.getAmount());

        createTransactionService.create(new CreateTransactionDTO(
                chargeback.getCard().getAccount(),
                TransactionTypeEnum.CREDIT,
                chargeback.getAmount(),
                "CHARGEBACK APPROVED: " + chargeback.getReason()
        ));

        Chargeback saved = chargebackRepository.save(chargeback);
        log.info("Chargeback approved: id={} amount={}", chargebackId, chargeback.getAmount());
        return saved;
    }

    public Chargeback reject(Long chargebackId) {
        Chargeback chargeback = chargebackRepository.findById(chargebackId).orElseThrow(() ->
                new CardNotFoundException("Chargeback " + chargebackId + " not found"));

        chargeback.setStatus(ChargebackStatus.REJECTED);
        chargeback.setReviewedAt(LocalDateTime.now());

        Chargeback saved = chargebackRepository.save(chargeback);
        log.info("Chargeback rejected: id={}", chargebackId);
        return saved;
    }
}
