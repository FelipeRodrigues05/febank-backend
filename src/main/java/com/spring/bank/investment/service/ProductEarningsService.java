package com.spring.bank.investment.service;

import com.spring.bank.investment.model.InvestmentPosition;
import com.spring.bank.investment.repository.InvestmentPositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductEarningsService {

    private static final BigDecimal CDI_ANNUAL_RATE = new BigDecimal("0.1065");
    private static final int TRADING_DAYS_IN_YEAR = 252;

    private final InvestmentPositionRepository investmentPositionRepository;

    public void processDailyEarnings() {
        List<InvestmentPosition> activePositions = investmentPositionRepository.findAll().stream()
                .filter(InvestmentPosition::isActive)
                .toList();

        activePositions.forEach(position -> {
            BigDecimal annualRate = position.getProduct().getAnnualRate();
            BigDecimal dailyRate = annualRate.divide(BigDecimal.valueOf(TRADING_DAYS_IN_YEAR), 10, RoundingMode.HALF_UP);
            BigDecimal dailyEarnings = position.getCurrentAmount().multiply(dailyRate)
                    .setScale(2, RoundingMode.HALF_UP);
            position.setCurrentAmount(position.getCurrentAmount().add(dailyEarnings));
        });

        investmentPositionRepository.saveAll(activePositions);
    }
}
