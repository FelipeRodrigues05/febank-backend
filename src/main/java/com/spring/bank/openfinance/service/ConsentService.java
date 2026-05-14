package com.spring.bank.openfinance.service;

import com.spring.bank.openfinance.dto.ConsentResponseDTO;
import com.spring.bank.openfinance.dto.CreateConsentDTO;
import com.spring.bank.openfinance.enums.ConsentPermission;
import com.spring.bank.openfinance.enums.ConsentStatus;
import com.spring.bank.openfinance.model.Consent;
import com.spring.bank.openfinance.repository.ConsentRepository;
import com.spring.bank.user.service.FindUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsentService {

    private final ConsentRepository consentRepository;
    private final FindUserService findUserService;

    public Consent create(CreateConsentDTO dto) {
        findUserService.getById(dto.userId());

        String permissionsString = dto.permissions().stream()
                .map(ConsentPermission::name)
                .collect(Collectors.joining(","));

        Consent consent = new Consent();
        consent.setUserId(dto.userId());
        consent.setClientId(dto.clientId());
        consent.setPermissions(permissionsString);
        consent.setStatus(ConsentStatus.AWAITING_AUTHORISATION);
        consent.setExpiresAt(dto.expiresAt());

        return consentRepository.save(consent);
    }

    public Consent authorise(String consentId, Long userId) {
        Consent consent = consentRepository.findById(consentId)
                .orElseThrow(() -> new IllegalArgumentException("Consent not found: " + consentId));

        if (!consent.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Consent does not belong to user: " + userId);
        }

        consent.setStatus(ConsentStatus.AUTHORISED);
        consent.setAuthorisedAt(LocalDateTime.now());

        return consentRepository.save(consent);
    }

    public void revoke(String consentId, Long userId) {
        Consent consent = consentRepository.findById(consentId)
                .orElseThrow(() -> new IllegalArgumentException("Consent not found: " + consentId));

        if (!consent.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Consent does not belong to user: " + userId);
        }

        consent.setStatus(ConsentStatus.REVOKED);
        consent.setRevokedAt(LocalDateTime.now());

        consentRepository.save(consent);
    }

    public List<ConsentResponseDTO> listByUser(Long userId) {
        return consentRepository.findByUserIdAndStatus(userId, ConsentStatus.AUTHORISED).stream()
                .map(ConsentResponseDTO::new)
                .toList();
    }
}
