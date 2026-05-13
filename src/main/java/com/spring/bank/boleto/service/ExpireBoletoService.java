package com.spring.bank.boleto.service;

import com.spring.bank.boleto.enums.BoletoStatus;
import com.spring.bank.boleto.model.Boleto;
import com.spring.bank.boleto.repository.BoletoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpireBoletoService {

    private final BoletoRepository boletoRepository;

    public void expireOverdue() {
        List<Boleto> overdueBoletos = boletoRepository.findByStatusAndDueDateBefore(
                BoletoStatus.PENDING, LocalDate.now()
        );

        overdueBoletos.forEach(boleto -> boleto.setStatus(BoletoStatus.EXPIRED));

        boletoRepository.saveAll(overdueBoletos);
    }
}
