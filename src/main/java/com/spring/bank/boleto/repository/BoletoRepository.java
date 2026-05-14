package com.spring.bank.boleto.repository;

import com.spring.bank.boleto.enums.BoletoStatus;
import com.spring.bank.boleto.model.Boleto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BoletoRepository extends JpaRepository<Boleto, Long> {

    Optional<Boleto> findByBoletoCode(String code);

    List<Boleto> findByIssuerAccountId(Long accountId);

    List<Boleto> findByStatusAndDueDateBefore(BoletoStatus status, LocalDate date);
}
