package com.spring.bank.pix.scheduler;

import com.spring.bank.pix.service.ProcessScheduledPixService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PixScheduler {

    private static final Logger log = LoggerFactory.getLogger(PixScheduler.class);

    private final ProcessScheduledPixService processScheduledPixService;

    @Scheduled(cron = "${app.scheduling.cron:0 0 8 * * *}")
    public void processScheduledPix() {
        log.info("PixScheduler: processing scheduled PIX transactions");
        try {
            processScheduledPixService.processToday();
        } catch (Exception error) {
            log.error("PixScheduler: error processing scheduled PIX: {}", error.getMessage(), error);
        }
    }
}
