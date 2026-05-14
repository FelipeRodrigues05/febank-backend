package com.spring.bank.pix.repository;

import com.spring.bank.pix.model.PixKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PixKeyRepository extends JpaRepository<PixKey, String> {
    Optional<PixKey> findByKey(String key);
    boolean existsByKey(String key);
    long countByAccountId(Long accountId);
    List<PixKey> findAllByAccountId(Long accountId);
}
