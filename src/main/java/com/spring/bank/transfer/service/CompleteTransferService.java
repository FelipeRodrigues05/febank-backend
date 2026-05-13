package com.spring.bank.transfer.service;

import com.spring.bank.common.exception.TransferNotFoundException;
import com.spring.bank.transfer.enums.TransferStatusEnum;
import com.spring.bank.transfer.model.Transfer;
import com.spring.bank.transfer.repository.TransferRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompleteTransferService {

    private static final Logger log = LoggerFactory.getLogger(CompleteTransferService.class);

    private final TransferRepository transferRepository;

    @Transactional
    public void complete(Long transferId) {
        Transfer transfer = transferRepository.findById(transferId).orElseThrow(() ->
                new TransferNotFoundException(String.format("Transfer with ID %s not found", transferId))
        );

        transfer.setStatus(TransferStatusEnum.COMPLETED);
        transferRepository.save(transfer);
        log.info("Transfer completed: id={}", transferId);
    }
}
