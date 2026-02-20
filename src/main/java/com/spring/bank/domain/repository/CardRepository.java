package com.spring.bank.domain.repository;

import com.spring.bank.domain.model.Account;
import com.spring.bank.domain.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, String> {
    List<Card> findAllByAccount(Account account);
    Optional<Card> findByNumber(String number);
}
