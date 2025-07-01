package com.spring.bank.domain.repository;

import com.spring.bank.domain.model.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransferRepository extends JpaRepository<Transfer, UUID> {
}
