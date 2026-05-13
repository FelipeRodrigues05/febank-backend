package com.spring.bank.card.repository;

import com.spring.bank.card.model.VirtualCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VirtualCardRepository extends JpaRepository<VirtualCard, Long> {
    List<VirtualCard> findByCardAccountId(Long accountId);
    Optional<VirtualCard> findByCardId(String cardId);
}
