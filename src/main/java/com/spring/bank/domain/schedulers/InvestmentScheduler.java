package com.spring.bank.domain.schedulers;

import com.spring.bank.domain.service.InvestmentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InvestmentScheduler {

    private static final Logger log = LoggerFactory.getLogger(InvestmentScheduler.class);

    private final InvestmentService investmentService;

    @Scheduled(cron = "${app.investment.scheduler.cron:0 0 0 * * *}")
    public void applyDailyEarnings() {
        log.info("InvestmentScheduler: starting daily earnings processing");
        try {
            investmentService.processDailyEarnings();
            log.info("InvestmentScheduler: daily earnings processing completed");
        } catch (Exception e) {
            log.error("InvestmentScheduler: error during daily earnings processing: {}", e.getMessage(), e);
        }
    }
}
