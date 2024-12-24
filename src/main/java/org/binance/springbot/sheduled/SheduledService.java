package org.binance.springbot.sheduled;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SheduledService {

    @Autowired
   // private OrdersRepository ordersRepository;

    //    @Scheduled(fixedRateString = "${fixedDelay.in.milliseconds}")
    @Scheduled(cron = "${cron.expression}")
    @Async
    public void scheduleFixedRateTask() {
        System.out.println(
                "Fixed rate task - " + System.currentTimeMillis() / 1000);
    }
}