package com.spring.bank.investment.scheduler;

import com.spring.bank.investment.service.DailyEarningsService;
import com.spring.bank.investment.service.ProductEarningsService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InvestmentScheduler {

    private static final Logger log = LoggerFactory.getLogger(InvestmentScheduler.class);

    private final DailyEarningsService dailyEarningsService;
    private final ProductEarningsService productEarningsService;

    @Scheduled(cron = "${app.investment.scheduler.cron:0 0 0 * * *}")
    public void applyDailyEarnings() {
        log.info("InvestmentScheduler: starting daily earnings processing");
        try {
            dailyEarningsService.processEarnings();
            log.info("InvestmentScheduler: daily earnings processing completed");
        } catch (Exception error) {
            log.error("InvestmentScheduler: error during daily earnings processing: {}", error.getMessage(), error);
        }
    }

    @Scheduled(cron = "${app.investment.scheduler.cron:0 0 0 * * *}")
    public void applyProductEarnings() {
        log.info("InvestmentScheduler: starting product earnings processing");
        try {
            productEarningsService.processDailyEarnings();
            log.info("InvestmentScheduler: product earnings processing completed");
        } catch (Exception error) {
            log.error("InvestmentScheduler: error during product earnings processing: {}", error.getMessage(), error);
        }
    }
}
