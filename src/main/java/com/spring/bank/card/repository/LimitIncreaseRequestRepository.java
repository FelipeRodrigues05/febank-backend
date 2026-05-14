package com.spring.bank.card.repository;

import com.spring.bank.card.model.LimitIncreaseRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LimitIncreaseRequestRepository extends JpaRepository<LimitIncreaseRequest, Long> {
    List<LimitIncreaseRequest> findByCardId(String cardId);
}
