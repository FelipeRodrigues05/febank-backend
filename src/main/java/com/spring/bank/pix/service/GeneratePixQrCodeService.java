package com.spring.bank.pix.service;

import com.spring.bank.account.enums.AccountStatusEnum;
import com.spring.bank.account.model.Account;
import com.spring.bank.account.service.FindAccountService;
import com.spring.bank.pix.dto.CreatePixQrCodeDTO;
import com.spring.bank.pix.enums.PixQrCodeType;
import com.spring.bank.pix.model.PixKey;
import com.spring.bank.pix.model.PixQrCode;
import com.spring.bank.pix.repository.PixQrCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GeneratePixQrCodeService {

    private final PixQrCodeRepository pixQrCodeRepository;
    private final FindAccountService findAccountService;
    private final FindPixKeyService findPixKeyService;

    public PixQrCode generate(CreatePixQrCodeDTO dto) {
        Account account = findAccountService.getById(dto.accountId());

        if (account.getStatus() != AccountStatusEnum.ACTIVE) {
            throw new IllegalArgumentException("Account is not active");
        }

        PixKey pixKeyObj = findPixKeyService.getByKey(dto.pixKey());

        if (!pixKeyObj.getAccount().getId().equals(dto.accountId())) {
            throw new IllegalArgumentException("PIX key does not belong to the specified account");
        }

        String qrId = UUID.randomUUID().toString();
        String payload = "PIX:" + dto.pixKey()
                + "|" + (dto.amount() != null ? dto.amount() : "OPEN")
                + "|" + (dto.description() != null ? dto.description() : "")
                + "|" + qrId;

        LocalDateTime expiresAt = null;
        if (dto.type() == PixQrCodeType.DYNAMIC && dto.expiryMinutes() != null) {
            expiresAt = LocalDateTime.now().plusMinutes(dto.expiryMinutes());
        }

        PixQrCode qrCode = new PixQrCode();
        qrCode.setAccount(account);
        qrCode.setType(dto.type());
        qrCode.setPixKey(dto.pixKey());
        qrCode.setAmount(dto.amount());
        qrCode.setDescription(dto.description());
        qrCode.setPayload(payload);
        qrCode.setActive(true);
        qrCode.setExpiresAt(expiresAt);

        return pixQrCodeRepository.save(qrCode);
    }

    public void deactivate(String qrCodeId, Long accountId) {
        PixQrCode qrCode = pixQrCodeRepository.findById(qrCodeId)
                .orElseThrow(() -> new IllegalArgumentException("QR Code not found: " + qrCodeId));

        if (!qrCode.getAccount().getId().equals(accountId)) {
            throw new IllegalArgumentException("QR Code does not belong to the specified account");
        }

        qrCode.setActive(false);
        pixQrCodeRepository.save(qrCode);
    }
}
