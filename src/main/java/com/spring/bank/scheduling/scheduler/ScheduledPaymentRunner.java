package com.spring.bank.scheduling.scheduler;

import com.spring.bank.scheduling.service.ProcessScheduledPaymentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledPaymentRunner {

    private final ProcessScheduledPaymentsService processScheduledPaymentsService;

    @Scheduled(cron = "${app.scheduling.cron:0 0 8 * * *}")
    public void run() {
        processScheduledPaymentsService.processToday();
    }
}
