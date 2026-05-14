package com.spring.bank.pix.service;

import com.spring.bank.account.enums.AccountStatusEnum;
import com.spring.bank.account.model.Account;
import com.spring.bank.account.service.FindAccountService;
import com.spring.bank.pix.dto.CreateScheduledPixDTO;
import com.spring.bank.pix.dto.ScheduledPixResponseDTO;
import com.spring.bank.pix.enums.ScheduledPixStatus;
import com.spring.bank.pix.model.ScheduledPix;
import com.spring.bank.pix.repository.ScheduledPixRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SchedulePixService {

    private final ScheduledPixRepository scheduledPixRepository;
    private final FindAccountService findAccountService;

    public ScheduledPix schedule(CreateScheduledPixDTO dto) {
        Account account = findAccountService.getById(dto.fromAccountId());

        if (account.getStatus() != AccountStatusEnum.ACTIVE) {
            throw new IllegalArgumentException("Account is not active");
        }

        if (!dto.scheduledDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Scheduled date must be in the future");
        }

        ScheduledPix scheduledPix = new ScheduledPix();
        scheduledPix.setFromAccount(account);
        scheduledPix.setPixKey(dto.pixKey());
        scheduledPix.setAmount(dto.amount());
        scheduledPix.setDescription(dto.description());
        scheduledPix.setScheduledDate(dto.scheduledDate());
        scheduledPix.setStatus(ScheduledPixStatus.PENDING);

        return scheduledPixRepository.save(scheduledPix);
    }

    public void cancel(Long id, Long accountId) {
        ScheduledPix scheduledPix = scheduledPixRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Scheduled PIX not found: " + id));

        if (!scheduledPix.getFromAccount().getId().equals(accountId)) {
            throw new IllegalArgumentException("Scheduled PIX does not belong to the specified account");
        }

        scheduledPix.setStatus(ScheduledPixStatus.CANCELLED);
        scheduledPixRepository.save(scheduledPix);
    }

    public List<ScheduledPixResponseDTO> listByAccount(Long accountId) {
        return scheduledPixRepository.findByFromAccountId(accountId)
                .stream()
                .map(ScheduledPixResponseDTO::new)
                .toList();
    }
}
