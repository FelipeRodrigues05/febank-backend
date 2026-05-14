package com.spring.bank.card.repository;

import com.spring.bank.card.model.Card;
import com.spring.bank.card.model.CardInstallmentPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardInstallmentPlanRepository extends JpaRepository<CardInstallmentPlan, Long> {
    List<CardInstallmentPlan> findByCardAndPaidInstallmentsLessThan(Card card, int totalInstallments);
}
