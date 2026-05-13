package com.spring.bank.pix.repository;

import com.spring.bank.pix.model.PixSaque;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PixSaqueRepository extends JpaRepository<PixSaque, Long> {
    List<PixSaque> findByAccountId(Long accountId);
}
