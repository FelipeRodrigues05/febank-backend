package com.spring.bank.pix.repository;

import com.spring.bank.pix.model.PixLimit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PixLimitRepository extends JpaRepository<PixLimit, Long> {
    Optional<PixLimit> findByAccountId(Long accountId);
}
