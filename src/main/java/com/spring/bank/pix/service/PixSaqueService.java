package com.spring.bank.pix.service;

import com.spring.bank.account.model.Account;
import com.spring.bank.account.service.AccountFundsService;
import com.spring.bank.account.service.FindAccountService;
import com.spring.bank.common.exception.InsufficientFundsException;
import com.spring.bank.pix.dto.CreatePixSaqueDTO;
import com.spring.bank.pix.enums.PixSaqueType;
import com.spring.bank.pix.model.PixSaque;
import com.spring.bank.pix.repository.PixSaqueRepository;
import com.spring.bank.transaction.dto.CreateTransactionDTO;
import com.spring.bank.transaction.enums.TransactionTypeEnum;
import com.spring.bank.transaction.service.CreateTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PixSaqueService {

    private final PixSaqueRepository pixSaqueRepository;
    private final FindAccountService findAccountService;
    private final AccountFundsService accountFundsService;
    private final CreateTransactionService createTransactionService;

    public PixSaque execute(CreatePixSaqueDTO dto) {
        Account account = findAccountService.getById(dto.accountId());
        Account merchantAccount = findAccountService.getById(dto.merchantAccountId());

        BigDecimal totalAmount = resolveTotalAmount(dto);

        if (account.getBalance().compareTo(totalAmount) < 0) {
            throw new InsufficientFundsException("Insufficient funds for PIX saque");
        }

        BigDecimal merchantReceiveAmount = resolveMerchantReceiveAmount(dto, totalAmount);
        String description = "PIX " + dto.type().name() + " via merchant #" + dto.merchantAccountId();

        accountFundsService.subtractFunds(dto.accountId(), totalAmount);
        accountFundsService.addFunds(dto.merchantAccountId(), merchantReceiveAmount);

        createTransactionService.create(new CreateTransactionDTO(account, TransactionTypeEnum.DEBIT, totalAmount, description));

        PixSaque pixSaque = new PixSaque();
        pixSaque.setAccount(account);
        pixSaque.setType(dto.type());
        pixSaque.setTotalAmount(totalAmount);
        pixSaque.setWithdrawalAmount(dto.withdrawalAmount());
        pixSaque.setMerchantAccountId(merchantAccount.getId());

        return pixSaqueRepository.save(pixSaque);
    }

    private BigDecimal resolveTotalAmount(CreatePixSaqueDTO dto) {
        if (dto.type() == PixSaqueType.SAQUE) {
            return dto.withdrawalAmount();
        }

        if (dto.purchaseAmount() == null) {
            throw new IllegalArgumentException("Purchase amount is required for TROCO operations");
        }

        return dto.purchaseAmount().add(dto.withdrawalAmount());
    }

    private BigDecimal resolveMerchantReceiveAmount(CreatePixSaqueDTO dto, BigDecimal totalAmount) {
        if (dto.type() == PixSaqueType.SAQUE) {
            return totalAmount;
        }
        return totalAmount.subtract(dto.withdrawalAmount());
    }
}
