package com.spring.bank.compliance.service;

import com.spring.bank.account.model.Account;
import com.spring.bank.account.repository.AccountRepository;
import com.spring.bank.compliance.enums.KycStatus;
import com.spring.bank.compliance.model.KycRecord;
import com.spring.bank.compliance.repository.KycRepository;
import com.spring.bank.user.service.FindUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CreditScoreService {

    private static final int BASE_SCORE = 400;
    private static final int UNVERIFIED_SCORE = 300;
    private static final int KYC_APPROVED_BONUS = 100;
    private static final int MULTIPLE_ACCOUNTS_BONUS = 100;
    private static final int BALANCE_TIER_ONE_BONUS = 200;
    private static final int BALANCE_TIER_TWO_BONUS = 100;
    private static final int ACCOUNT_TENURE_BONUS = 100;
    private static final int MAX_SCORE = 1000;
    private static final int MIN_ACCOUNTS_FOR_BONUS = 2;
    private static final int ACCOUNT_TENURE_MONTHS = 6;
    private static final BigDecimal BALANCE_TIER_ONE_THRESHOLD = new BigDecimal("1000");
    private static final BigDecimal BALANCE_TIER_TWO_THRESHOLD = new BigDecimal("5000");

    private final KycRepository kycRepository;
    private final AccountRepository accountRepository;
    private final FindUserService findUserService;

    public int calculate(Long userId) {
        findUserService.getById(userId);

        Optional<KycRecord> kycRecord = kycRepository.findByUserId(userId);
        if (kycRecord.isEmpty() || kycRecord.get().getStatus() != KycStatus.APPROVED) {
            return UNVERIFIED_SCORE;
        }

        List<Account> accounts = accountRepository.findByUserId(userId);

        if (accounts.isEmpty()) {
            return UNVERIFIED_SCORE;
        }

        int score = BASE_SCORE;
        score += KYC_APPROVED_BONUS;

        if (accounts.size() >= MIN_ACCOUNTS_FOR_BONUS) {
            score += MULTIPLE_ACCOUNTS_BONUS;
        }

        boolean hasTierOneBalance = accounts.stream()
                .anyMatch(account -> account.getBalance() != null
                        && account.getBalance().compareTo(BALANCE_TIER_ONE_THRESHOLD) > 0);

        if (hasTierOneBalance) {
            score += BALANCE_TIER_ONE_BONUS;
        }

        boolean hasTierTwoBalance = accounts.stream()
                .anyMatch(account -> account.getBalance() != null
                        && account.getBalance().compareTo(BALANCE_TIER_TWO_THRESHOLD) > 0);

        if (hasTierTwoBalance) {
            score += BALANCE_TIER_TWO_BONUS;
        }

        LocalDateTime tenureThreshold = LocalDateTime.now().minusMonths(ACCOUNT_TENURE_MONTHS);
        boolean hasLongStandingAccount = accounts.stream()
                .anyMatch(account -> account.getCreatedAt() != null
                        && account.getCreatedAt().isBefore(tenureThreshold));

        if (hasLongStandingAccount) {
            score += ACCOUNT_TENURE_BONUS;
        }

        return Math.min(MAX_SCORE, score);
    }
}
