package com.spring.bank.domain.schedulers;

import com.spring.bank.domain.service.InvestmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InvestmentScheduler {
    private final InvestmentService investmentService;

    @Scheduled(fixedRate = 86_400_000) // 24h
//    @Scheduled(fixedRate = 10000) // 10s
    public void applyDailyEarnings() {
        investmentService.processDailyEarnings();
    }
}
