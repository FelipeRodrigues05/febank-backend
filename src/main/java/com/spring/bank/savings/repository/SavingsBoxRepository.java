package com.spring.bank.savings.repository;

import com.spring.bank.savings.model.SavingsBox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavingsBoxRepository extends JpaRepository<SavingsBox, Long> {
    List<SavingsBox> findAllByAccountId(Long accountId);
}
