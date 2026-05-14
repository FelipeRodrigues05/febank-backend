package com.spring.bank.investment.repository;

import com.spring.bank.investment.model.InvestmentPosition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvestmentPositionRepository extends JpaRepository<InvestmentPosition, Long> {

    List<InvestmentPosition> findByAccountIdAndActiveTrue(Long accountId);

    Optional<InvestmentPosition> findByIdAndAccountId(Long id, Long accountId);
}
