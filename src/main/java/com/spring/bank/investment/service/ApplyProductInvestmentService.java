package com.spring.bank.investment.service;

import com.spring.bank.account.model.Account;
import com.spring.bank.account.service.AccountFundsService;
import com.spring.bank.account.service.FindAccountService;
import com.spring.bank.investment.dto.ApplyProductInvestmentDTO;
import com.spring.bank.investment.model.InvestmentPosition;
import com.spring.bank.investment.model.InvestmentProduct;
import com.spring.bank.investment.repository.InvestmentPositionRepository;
import com.spring.bank.transaction.dto.CreateTransactionDTO;
import com.spring.bank.transaction.enums.TransactionTypeEnum;
import com.spring.bank.transaction.service.CreateTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplyProductInvestmentService {

    private final InvestmentPositionRepository investmentPositionRepository;
    private final FindAccountService findAccountService;
    private final AccountFundsService accountFundsService;
    private final CreateTransactionService createTransactionService;
    private final InvestmentProductCatalogService investmentProductCatalogService;

    public InvestmentPosition apply(ApplyProductInvestmentDTO dto) {
        Account account = findAccountService.getById(dto.accountId());
        InvestmentProduct product = investmentProductCatalogService.getById(dto.productId());

        if (dto.amount().compareTo(product.getMinimumAmount()) < 0) {
            throw new IllegalArgumentException("Minimum investment is R$ " + product.getMinimumAmount());
        }

        accountFundsService.subtractFunds(dto.accountId(), dto.amount());

        createTransactionService.create(new CreateTransactionDTO(
                account,
                TransactionTypeEnum.DEBIT,
                dto.amount(),
                "APPLIED TO " + product.getName()
        ));

        InvestmentPosition position = new InvestmentPosition();
        position.setAccount(account);
        position.setProduct(product);
        position.setInvestedAmount(dto.amount());
        position.setCurrentAmount(dto.amount());
        position.setActive(true);

        return investmentPositionRepository.save(position);
    }
}
