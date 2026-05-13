package com.spring.bank.boleto.scheduler;

import com.spring.bank.boleto.service.ExpireBoletoService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BoletoScheduler {

    private static final Logger log = LoggerFactory.getLogger(BoletoScheduler.class);

    private final ExpireBoletoService expireBoletoService;

    @Scheduled(cron = "0 0 1 * * *")
    public void expireOverdueBoletos() {
        log.info("BoletoScheduler: expiring overdue boletos");
        try {
            expireBoletoService.expireOverdue();
        } catch (Exception error) {
            log.error("BoletoScheduler: error expiring boletos: {}", error.getMessage(), error);
        }
    }
}
