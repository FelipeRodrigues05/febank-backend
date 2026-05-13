package com.spring.bank.openfinance.service;

import com.spring.bank.account.model.Account;
import com.spring.bank.account.service.FindAccountService;
import com.spring.bank.openfinance.dto.AccountDataResponseDTO;
import com.spring.bank.openfinance.enums.ConsentStatus;
import com.spring.bank.openfinance.model.Consent;
import com.spring.bank.openfinance.repository.ConsentRepository;
import com.spring.bank.user.model.User;
import com.spring.bank.user.service.FindUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenFinanceDataService {

    private final ConsentRepository consentRepository;
    private final FindUserService findUserService;
    private final FindAccountService findAccountService;

    public List<AccountDataResponseDTO> getAccountData(String consentId, String clientId, Long userId) {
        Consent consent = consentRepository.findByIdAndClientId(consentId, clientId)
                .orElseThrow(() -> new IllegalArgumentException("Consent not found or unauthorized"));

        if (consent.getStatus() != ConsentStatus.AUTHORISED || consent.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Consent is not active or has expired");
        }

        boolean hasAccountsReadPermission = consent.getPermissions().contains("ACCOUNTS_READ");
        boolean hasBalancesReadPermission = consent.getPermissions().contains("BALANCES_READ");

        if (!hasAccountsReadPermission && !hasBalancesReadPermission) {
            throw new IllegalArgumentException("Consent does not have required permissions");
        }

        User user = findUserService.getById(userId);

        return user.getAccounts().stream()
                .map(account -> new AccountDataResponseDTO(
                        account.getId(),
                        account.getNumber(),
                        account.getType() != null ? account.getType().name() : null,
                        account.getBalance(),
                        user.getName()
                ))
                .toList();
    }
}
