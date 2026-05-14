package com.spring.bank.domain.schedulers;

import com.spring.bank.domain.service.CardBillService;
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

    private final CardBillService cardBillService;

    @Scheduled(cron = "${app.card.bill.close.cron:0 0 0 1 * *}")
    public void closePreviousMonthBills() {
        YearMonth previous = YearMonth.now().minusMonths(1);
        log.info("Closing bills for {}/{}", previous.getMonthValue(), previous.getYear());
        cardBillService.closeBills(previous.getMonthValue(), previous.getYear());
    }
}
