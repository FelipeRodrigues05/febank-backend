package com.spring.bank.domain.repository;

import com.spring.bank.domain.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findByNumber(String number);

    boolean existsByNumber(String number);
}
