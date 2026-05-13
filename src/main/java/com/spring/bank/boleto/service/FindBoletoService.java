package com.spring.bank.boleto.service;

import com.spring.bank.boleto.model.Boleto;
import com.spring.bank.boleto.repository.BoletoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FindBoletoService {

    private final BoletoRepository boletoRepository;

    public Boleto getByCode(String code) {
        return boletoRepository.findByBoletoCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Boleto not found: " + code));
    }

    public List<Boleto> listByIssuer(Long accountId) {
        return boletoRepository.findByIssuerAccountId(accountId);
    }
}
