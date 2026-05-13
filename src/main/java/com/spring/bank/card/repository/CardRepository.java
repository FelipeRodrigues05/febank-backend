package com.spring.bank.card.repository;

import com.spring.bank.account.model.Account;
import com.spring.bank.card.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, String> {
    Optional<Card> findByNumber(String number);
    List<Card> findAllByAccount(Account account);
}
