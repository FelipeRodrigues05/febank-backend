package com.spring.bank.boleto.service;

import com.spring.bank.account.enums.AccountStatusEnum;
import com.spring.bank.account.model.Account;
import com.spring.bank.account.service.FindAccountService;
import com.spring.bank.boleto.dto.EmitBoletoDTO;
import com.spring.bank.boleto.enums.BoletoStatus;
import com.spring.bank.boleto.model.Boleto;
import com.spring.bank.boleto.repository.BoletoRepository;
import com.spring.bank.boleto.utils.BoletoCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmitBoletoService {

    private final BoletoRepository boletoRepository;
    private final FindAccountService findAccountService;
    private final BoletoCodeGenerator boletoCodeGenerator;

    public Boleto emit(EmitBoletoDTO dto) {
        Account issuerAccount = findAccountService.getById(dto.issuerAccountId());

        if (issuerAccount.getStatus() != AccountStatusEnum.ACTIVE) {
            throw new IllegalArgumentException("Issuer account is not active");
        }

        String generatedCode = boletoCodeGenerator.generate(dto.amount(), dto.dueDate());

        Boleto boleto = new Boleto();
        boleto.setIssuerAccountId(dto.issuerAccountId());
        boleto.setPayerDocument(dto.payerDocument());
        boleto.setAmount(dto.amount());
        boleto.setDescription(dto.description());
        boleto.setBoletoCode(generatedCode);
        boleto.setStatus(BoletoStatus.PENDING);
        boleto.setDueDate(dto.dueDate());

        return boletoRepository.save(boleto);
    }
}
