package com.spring.bank.pix.service;

import com.spring.bank.account.model.Account;
import com.spring.bank.account.service.FindAccountService;
import com.spring.bank.pix.dto.PixLimitResponseDTO;
import com.spring.bank.pix.dto.UpdatePixLimitDTO;
import com.spring.bank.pix.model.PixLimit;
import com.spring.bank.pix.repository.PixLimitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PixLimitService {

    private static final BigDecimal DEFAULT_DAILY_LIMIT = new BigDecimal("5000.00");
    private static final BigDecimal DEFAULT_SINGLE_LIMIT = new BigDecimal("1000.00");
    private static final BigDecimal DEFAULT_NIGHT_LIMIT = new BigDecimal("1000.00");

    private static final int NIGHT_START_HOUR = 20;
    private static final int NIGHT_END_HOUR = 6;

    private final PixLimitRepository pixLimitRepository;
    private final FindAccountService findAccountService;

    public PixLimit getOrCreateDefault(Long accountId) {
        return pixLimitRepository.findByAccountId(accountId).orElseGet(() -> {
            Account account = findAccountService.getById(accountId);
            PixLimit limit = new PixLimit();
            limit.setAccount(account);
            limit.setDailyLimit(DEFAULT_DAILY_LIMIT);
            limit.setSingleTransactionLimit(DEFAULT_SINGLE_LIMIT);
            limit.setNightLimit(DEFAULT_NIGHT_LIMIT);
            return pixLimitRepository.save(limit);
        });
    }

    public PixLimitResponseDTO update(Long accountId, UpdatePixLimitDTO dto) {
        PixLimit limit = getOrCreateDefault(accountId);
        limit.setDailyLimit(dto.dailyLimit());
        limit.setSingleTransactionLimit(dto.singleTransactionLimit());
        limit.setNightLimit(dto.nightLimit());
        return new PixLimitResponseDTO(pixLimitRepository.save(limit));
    }

    public void checkLimit(Long accountId, BigDecimal amount) {
        PixLimit limit = getOrCreateDefault(accountId);

        if (amount.compareTo(limit.getSingleTransactionLimit()) > 0) {
            throw new IllegalArgumentException("Amount exceeds your PIX single transaction limit");
        }

        int currentHour = LocalDateTime.now().getHour();
        boolean isNightPeriod = currentHour >= NIGHT_START_HOUR || currentHour < NIGHT_END_HOUR;

        if (isNightPeriod && amount.compareTo(limit.getNightLimit()) > 0) {
            throw new IllegalArgumentException("Amount exceeds your PIX night limit (R$ " + limit.getNightLimit() + ")");
        }
    }
}
