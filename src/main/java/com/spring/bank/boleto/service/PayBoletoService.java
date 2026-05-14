package com.spring.bank.boleto.service;

import com.spring.bank.account.model.Account;
import com.spring.bank.account.service.AccountFundsService;
import com.spring.bank.account.service.FindAccountService;
import com.spring.bank.boleto.dto.PayBoletoDTO;
import com.spring.bank.boleto.enums.BoletoStatus;
import com.spring.bank.boleto.model.Boleto;
import com.spring.bank.boleto.repository.BoletoRepository;
import com.spring.bank.common.exception.InsufficientFundsException;
import com.spring.bank.transaction.dto.CreateTransactionDTO;
import com.spring.bank.transaction.enums.TransactionTypeEnum;
import com.spring.bank.transaction.service.CreateTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PayBoletoService {

    private final BoletoRepository boletoRepository;
    private final FindAccountService findAccountService;
    private final AccountFundsService accountFundsService;
    private final CreateTransactionService createTransactionService;

    public Boleto pay(PayBoletoDTO dto) {
        Boleto boleto = boletoRepository.findByBoletoCode(dto.boletoCode())
                .orElseThrow(() -> new IllegalArgumentException("Boleto not found"));

        if (boleto.getStatus() != BoletoStatus.PENDING) {
            throw new IllegalArgumentException("Boleto is not payable (status: " + boleto.getStatus() + ")");
        }

        if (boleto.getDueDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Boleto has expired");
        }

        Account payerAccount = findAccountService.getById(dto.payerAccountId());

        if (payerAccount.getBalance().compareTo(boleto.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds to pay boleto");
        }

        accountFundsService.subtractFunds(dto.payerAccountId(), boleto.getAmount());
        accountFundsService.addFunds(boleto.getIssuerAccountId(), boleto.getAmount());

        Account issuerAccount = findAccountService.getById(boleto.getIssuerAccountId());

        createTransactionService.create(new CreateTransactionDTO(
                payerAccount,
                TransactionTypeEnum.DEBIT,
                boleto.getAmount(),
                "BOLETO: " + boleto.getDescription()
        ));

        createTransactionService.create(new CreateTransactionDTO(
                issuerAccount,
                TransactionTypeEnum.CREDIT,
                boleto.getAmount(),
                "BOLETO RECEIVED: " + boleto.getDescription()
        ));

        boleto.setStatus(BoletoStatus.PAID);
        boleto.setPaidAt(LocalDateTime.now());
        boleto.setPaidByAccountId(dto.payerAccountId());

        return boletoRepository.save(boleto);
    }
}
