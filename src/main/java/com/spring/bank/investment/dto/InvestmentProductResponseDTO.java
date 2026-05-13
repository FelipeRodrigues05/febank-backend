package com.spring.bank.investment.dto;

import com.spring.bank.investment.enums.ProductType;
import com.spring.bank.investment.model.InvestmentProduct;

import java.math.BigDecimal;

public record InvestmentProductResponseDTO(
        Long id,
        String name,
        ProductType type,
        BigDecimal annualRate,
        BigDecimal cdiPercentage,
        BigDecimal minimumAmount,
        int minimumDaysToRedeem,
        boolean irExempt
) {
    public InvestmentProductResponseDTO(InvestmentProduct product) {
        this(
                product.getId(),
                product.getName(),
                product.getType(),
                product.getAnnualRate(),
                product.getCdiPercentage(),
                product.getMinimumAmount(),
                product.getMinimumDaysToRedeem(),
                product.isIrExempt()
        );
    }
}
