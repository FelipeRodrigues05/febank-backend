package com.spring.bank.card.scheduler;

import com.spring.bank.card.service.CloseCardBillService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.YearMonth;

@Component
@RequiredArgsConstructor
public class CardBillScheduler {

    private static final Logger log = LoggerFactory.getLogger(CardBillScheduler.class);

    private final CloseCardBillService closeCardBillService;

    @Scheduled(cron = "${app.card.bill.close.cron:0 0 0 1 * *}")
    public void closePreviousMonthBills() {
        YearMonth previous = YearMonth.now().minusMonths(1);
        log.info("Closing bills for {}/{}", previous.getMonthValue(), previous.getYear());
        closeCardBillService.closeBills(previous.getMonthValue(), previous.getYear());
    }
}
