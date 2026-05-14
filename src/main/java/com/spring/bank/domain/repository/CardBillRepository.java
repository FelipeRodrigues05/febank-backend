package com.spring.bank.domain.repository;

import com.spring.bank.domain.enums.card.CardBillStatus;
import com.spring.bank.domain.model.CardBill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardBillRepository extends JpaRepository<CardBill, Long> {
    Optional<CardBill> findByCardIdAndMonthAndYear(String cardId, int month, int year);
    List<CardBill> findAllByCardIdOrderByYearDescMonthDesc(String cardId);
    List<CardBill> findAllByStatusAndMonthAndYear(CardBillStatus status, int month, int year);
}
