package com.spring.bank.pix.service;

import com.spring.bank.account.model.Account;
import com.spring.bank.account.service.AccountFundsService;
import com.spring.bank.account.service.FindAccountService;
import com.spring.bank.pix.dto.CreatePixDevolutionDTO;
import com.spring.bank.pix.dto.PixDevolutionResponseDTO;
import com.spring.bank.pix.enums.PixDevolutionStatus;
import com.spring.bank.pix.model.PixDevolution;
import com.spring.bank.pix.repository.PixDevolutionRepository;
import com.spring.bank.transaction.dto.CreateTransactionDTO;
import com.spring.bank.transaction.enums.TransactionTypeEnum;
import com.spring.bank.transaction.service.CreateTransactionService;
import com.spring.bank.transfer.model.Transfer;
import com.spring.bank.transfer.repository.TransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PixDevolutionService {

    private final PixDevolutionRepository pixDevolutionRepository;
    private final TransferRepository transferRepository;
    private final AccountFundsService accountFundsService;
    private final CreateTransactionService createTransactionService;
    private final FindAccountService findAccountService;

    public PixDevolution request(CreatePixDevolutionDTO dto) {
        Transfer transfer = transferRepository.findById(dto.originalTransferId())
                .orElseThrow(() -> new IllegalArgumentException("Original transfer not found"));

        if (dto.amount().compareTo(transfer.getAmount()) > 0) {
            throw new IllegalArgumentException("Devolution amount exceeds original transfer");
        }

        PixDevolution devolution = new PixDevolution();
        devolution.setOriginalTransferId(dto.originalTransferId());
        devolution.setRequesterAccountId(dto.requesterAccountId());
        devolution.setAmount(dto.amount());
        devolution.setReason(dto.reason());
        devolution.setStatus(PixDevolutionStatus.PENDING);

        return pixDevolutionRepository.save(devolution);
    }

    public PixDevolution approve(Long devolutionId) {
        PixDevolution devolution = pixDevolutionRepository.findById(devolutionId)
                .orElseThrow(() -> new IllegalArgumentException("Devolution not found: " + devolutionId));

        Transfer transfer = transferRepository.findById(devolution.getOriginalTransferId())
                .orElseThrow(() -> new IllegalArgumentException("Original transfer not found"));

        Account originalSender = transfer.getFromAccount();
        Account originalReceiver = transfer.getToAccount();

        String description = "PIX Devolution for transfer #" + transfer.getId();

        accountFundsService.subtractFunds(originalReceiver.getId(), devolution.getAmount());
        accountFundsService.addFunds(originalSender.getId(), devolution.getAmount());

        createTransactionService.create(new CreateTransactionDTO(originalSender, TransactionTypeEnum.CREDIT, devolution.getAmount(), description));

        devolution.setStatus(PixDevolutionStatus.APPROVED);
        devolution.setReviewedAt(LocalDateTime.now());

        return pixDevolutionRepository.save(devolution);
    }

    public PixDevolution reject(Long devolutionId) {
        PixDevolution devolution = pixDevolutionRepository.findById(devolutionId)
                .orElseThrow(() -> new IllegalArgumentException("Devolution not found: " + devolutionId));

        devolution.setStatus(PixDevolutionStatus.REJECTED);
        devolution.setReviewedAt(LocalDateTime.now());

        return pixDevolutionRepository.save(devolution);
    }

    public List<PixDevolutionResponseDTO> listByAccount(Long accountId) {
        return pixDevolutionRepository.findByRequesterAccountId(accountId)
                .stream()
                .map(PixDevolutionResponseDTO::new)
                .toList();
    }
}
