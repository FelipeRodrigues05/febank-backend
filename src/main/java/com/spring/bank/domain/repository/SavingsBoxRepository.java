package com.spring.bank.domain.repository;

import com.spring.bank.domain.model.SavingsBox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavingsBoxRepository extends JpaRepository<SavingsBox, Long> {
    List<SavingsBox> findAllByAccountId(Long accountId);
}
