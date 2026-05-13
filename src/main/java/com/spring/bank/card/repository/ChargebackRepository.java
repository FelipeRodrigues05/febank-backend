package com.spring.bank.card.repository;

import com.spring.bank.card.model.Chargeback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChargebackRepository extends JpaRepository<Chargeback, Long> {
    List<Chargeback> findByCardId(String cardId);
}
