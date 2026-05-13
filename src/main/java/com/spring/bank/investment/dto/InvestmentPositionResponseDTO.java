package com.spring.bank.investment.dto;

import com.spring.bank.investment.enums.ProductType;
import com.spring.bank.investment.model.InvestmentPosition;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InvestmentPositionResponseDTO(
        Long id,
        Long productId,
        String productName,
        ProductType productType,
        BigDecimal investedAmount,
        BigDecimal currentAmount,
        BigDecimal earnings,
        boolean irExempt,
        LocalDateTime appliedAt
) {
    public InvestmentPositionResponseDTO(InvestmentPosition position) {
        this(
                position.getId(),
                position.getProduct().getId(),
                position.getProduct().getName(),
                position.getProduct().getType(),
                position.getInvestedAmount(),
                position.getCurrentAmount(),
                position.getCurrentAmount().subtract(position.getInvestedAmount()),
                position.getProduct().isIrExempt(),
                position.getAppliedAt()
        );
    }
}
