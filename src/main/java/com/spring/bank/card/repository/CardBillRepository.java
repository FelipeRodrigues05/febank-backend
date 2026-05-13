package com.spring.bank.card.repository;

import com.spring.bank.card.enums.CardBillStatus;
import com.spring.bank.card.model.CardBill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardBillRepository extends JpaRepository<CardBill, Long> {
    Optional<CardBill> findByCardIdAndMonthAndYear(String cardId, int month, int year);
    List<CardBill> findAllByStatusAndMonthAndYear(CardBillStatus status, int month, int year);
    List<CardBill> findAllByCardIdOrderByYearDescMonthDesc(String cardId);
}
