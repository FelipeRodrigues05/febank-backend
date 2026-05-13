package com.spring.bank.pix.service;

import com.spring.bank.account.model.Account;
import com.spring.bank.account.service.AccountFundsService;
import com.spring.bank.account.service.FindAccountService;
import com.spring.bank.pix.enums.PixQrCodeType;
import com.spring.bank.pix.model.PixKey;
import com.spring.bank.pix.model.PixQrCode;
import com.spring.bank.pix.repository.PixQrCodeRepository;
import com.spring.bank.transaction.dto.CreateTransactionDTO;
import com.spring.bank.transaction.enums.TransactionTypeEnum;
import com.spring.bank.transaction.service.CreateTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PayPixQrCodeService {

    private final PixQrCodeRepository pixQrCodeRepository;
    private final FindAccountService findAccountService;
    private final AccountFundsService accountFundsService;
    private final CreateTransactionService createTransactionService;
    private final FindPixKeyService findPixKeyService;
    private final PixLimitService pixLimitService;

    public BigDecimal pay(Long payerAccountId, String qrCodeId, BigDecimal openAmount) {
        PixQrCode qrCode = pixQrCodeRepository.findByIdAndActiveTrue(qrCodeId)
                .orElseThrow(() -> new IllegalArgumentException("QR Code not found or expired"));

        if (qrCode.getType() == PixQrCodeType.DYNAMIC
                && qrCode.getExpiresAt() != null
                && qrCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("QR Code not found or expired");
        }

        BigDecimal effectiveAmount = qrCode.getAmount() != null ? qrCode.getAmount() : openAmount;

        if (effectiveAmount == null) {
            throw new IllegalArgumentException("Amount must be provided for open-value QR codes");
        }

        pixLimitService.checkLimit(payerAccountId, effectiveAmount);

        PixKey pixKey = findPixKeyService.getByKey(qrCode.getPixKey());
        Account receiverAccount = pixKey.getAccount();
        Account payerAccount = findAccountService.getById(payerAccountId);

        String description = "PIX QR Code: " + qrCodeId;

        accountFundsService.subtractFunds(payerAccountId, effectiveAmount);
        accountFundsService.addFunds(receiverAccount.getId(), effectiveAmount);

        createTransactionService.create(new CreateTransactionDTO(payerAccount, TransactionTypeEnum.DEBIT, effectiveAmount, description));
        createTransactionService.create(new CreateTransactionDTO(receiverAccount, TransactionTypeEnum.CREDIT, effectiveAmount, description));

        if (qrCode.getType() == PixQrCodeType.DYNAMIC) {
            qrCode.setActive(false);
            pixQrCodeRepository.save(qrCode);
        }

        return effectiveAmount;
    }
}
