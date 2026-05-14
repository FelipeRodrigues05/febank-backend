package com.spring.bank.pix.repository;

import com.spring.bank.pix.model.PixDevolution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PixDevolutionRepository extends JpaRepository<PixDevolution, Long> {
    List<PixDevolution> findByRequesterAccountId(Long accountId);
}
