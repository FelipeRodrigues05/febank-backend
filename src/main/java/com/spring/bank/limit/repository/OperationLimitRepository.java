package com.spring.bank.limit.repository;

import com.spring.bank.limit.enums.LimitType;
import com.spring.bank.limit.model.OperationLimit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OperationLimitRepository extends JpaRepository<OperationLimit, Long> {
    Optional<OperationLimit> findByUserIdAndLimitType(Long userId, LimitType type);
}
