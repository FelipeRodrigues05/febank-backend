package com.spring.bank.card.service;

import com.spring.bank.card.dto.InstallmentPlanResponseDTO;
import com.spring.bank.card.dto.InstallmentPurchaseDTO;
import com.spring.bank.card.enums.CardType;
import com.spring.bank.card.model.Card;
import com.spring.bank.card.model.CardInstallmentPlan;
import com.spring.bank.card.repository.CardInstallmentPlanRepository;
import com.spring.bank.card.repository.CardRepository;
import com.spring.bank.common.exception.CardNotFoundException;
import com.spring.bank.common.exception.InsufficientFundsException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InstallmentService {

    private static final Logger log = LoggerFactory.getLogger(InstallmentService.class);

    private final CardRepository cardRepository;
    private final CardInstallmentPlanRepository installmentPlanRepository;
    private final FindCardService findCardService;
    private final AddBillPurchaseService addBillPurchaseService;
    private final FindCardBillService findCardBillService;

    @Transactional
    public CardInstallmentPlan createInstallment(InstallmentPurchaseDTO dto) {
        Card card = findCardService.validateForPayment(dto.cardNumber(), dto.cvv(), CardType.CREDIT);

        BigDecimal installmentAmount = dto.amount().divide(BigDecimal.valueOf(dto.installments()), 2, RoundingMode.HALF_UP);

        if (card.getLimitAvailable().compareTo(dto.amount()) < 0) {
            throw new InsufficientFundsException("Insufficient credit limit for this installment purchase");
        }

        card.setLimitAvailable(card.getLimitAvailable().subtract(dto.amount()));
        card.setUsedLimit(card.getUsedLimit().add(dto.amount()));
        cardRepository.save(card);

        addBillPurchaseService.addPurchase(card, installmentAmount);

        CardInstallmentPlan plan = new CardInstallmentPlan();
        plan.setCard(card);
        plan.setCardBill(null);
        plan.setTotalAmount(dto.amount());
        plan.setInstallmentAmount(installmentAmount);
        plan.setTotalInstallments(dto.installments());
        plan.setPaidInstallments(0);
        plan.setDescription(dto.description());

        CardInstallmentPlan saved = installmentPlanRepository.save(plan);
        log.info("Installment plan created: id={} cardId={} total={} installments={}", saved.getId(), card.getId(), dto.amount(), dto.installments());
        return saved;
    }

    public List<InstallmentPlanResponseDTO> listActive(String cardId) {
        Card card = cardRepository.findById(cardId).orElseThrow(() ->
                new CardNotFoundException("Card " + cardId + " not found"));
        return installmentPlanRepository
                .findByCardAndPaidInstallmentsLessThan(card, Integer.MAX_VALUE)
                .stream()
                .filter(plan -> plan.getPaidInstallments() < plan.getTotalInstallments())
                .map(InstallmentPlanResponseDTO::new)
                .toList();
    }

    @Transactional
    public void processMonthlyInstallments(Card card) {
        List<CardInstallmentPlan> activePlans = installmentPlanRepository
                .findByCardAndPaidInstallmentsLessThan(card, Integer.MAX_VALUE)
                .stream()
                .filter(plan -> plan.getPaidInstallments() < plan.getTotalInstallments())
                .toList();

        for (CardInstallmentPlan plan : activePlans) {
            addBillPurchaseService.addPurchase(card, plan.getInstallmentAmount());
            plan.setPaidInstallments(plan.getPaidInstallments() + 1);
            installmentPlanRepository.save(plan);
            log.info("Monthly installment processed: planId={} paidInstallments={}/{}", plan.getId(), plan.getPaidInstallments(), plan.getTotalInstallments());
        }
    }
}
