package com.spring.bank.investment.service;

import com.spring.bank.account.model.Account;
import com.spring.bank.account.service.AccountFundsService;
import com.spring.bank.account.service.FindAccountService;
import com.spring.bank.investment.dto.RedeemProductDTO;
import com.spring.bank.investment.model.InvestmentPosition;
import com.spring.bank.investment.model.InvestmentProduct;
import com.spring.bank.investment.repository.InvestmentPositionRepository;
import com.spring.bank.transaction.dto.CreateTransactionDTO;
import com.spring.bank.transaction.enums.TransactionTypeEnum;
import com.spring.bank.transaction.service.CreateTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RedeemProductInvestmentService {

    private final InvestmentPositionRepository investmentPositionRepository;
    private final FindAccountService findAccountService;
    private final AccountFundsService accountFundsService;
    private final CreateTransactionService createTransactionService;
    private final InvestmentProductCatalogService investmentProductCatalogService;

    public InvestmentPosition redeem(RedeemProductDTO dto) {
        InvestmentPosition position = investmentPositionRepository
                .findByIdAndAccountId(dto.positionId(), dto.accountId())
                .orElseThrow(() -> new IllegalArgumentException("Position not found"));

        if (!position.isActive()) {
            throw new IllegalArgumentException("Position already redeemed");
        }

        InvestmentProduct product = position.getProduct();
        int minimumDays = product.getMinimumDaysToRedeem();
        LocalDateTime earliestRedeemDate = position.getAppliedAt().plusDays(minimumDays);

        if (earliestRedeemDate.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot redeem before " + minimumDays + " days");
        }

        Account account = findAccountService.getById(dto.accountId());
        accountFundsService.addFunds(dto.accountId(), position.getCurrentAmount());

        createTransactionService.create(new CreateTransactionDTO(
                account,
                TransactionTypeEnum.CREDIT,
                position.getCurrentAmount(),
                "REDEEMED FROM " + product.getName()
        ));

        position.setActive(false);
        position.setRedeemedAt(LocalDateTime.now());

        return investmentPositionRepository.save(position);
    }
}
