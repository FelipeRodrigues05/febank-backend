package com.spring.bank.domain.repository;

import com.spring.bank.domain.model.PixContact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PixContactRepository extends JpaRepository<PixContact, Long> {
    List<PixContact> findAllByAccountIdOrderByTransferCountDesc(Long accountId);
    Optional<PixContact> findByIdAndAccountId(Long id, Long accountId);
    Optional<PixContact> findByAccountIdAndPixKey(Long accountId, String pixKey);
}
