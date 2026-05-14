package com.spring.bank.domain.repository;

import com.spring.bank.domain.model.PixKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PixKeyRepository extends JpaRepository<PixKey, String> {
    boolean existsByKey(String key);
    Optional<PixKey> findByKey(String key);
    List<PixKey> findAllByAccountId(Long accountId);
    long countByAccountId(Long accountId);
}
