package com.spring.bank.openfinance.repository;

import com.spring.bank.openfinance.enums.ConsentStatus;
import com.spring.bank.openfinance.model.Consent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConsentRepository extends JpaRepository<Consent, String> {

    List<Consent> findByUserIdAndStatus(Long userId, ConsentStatus status);

    Optional<Consent> findByIdAndClientId(String id, String clientId);
}
