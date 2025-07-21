package org.binance.springbot.service;


import org.springframework.stereotype.Service;

@Service
public class MarketSignalService {

    private final NotificationService notificationService;

    public MarketSignalService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void onBuySignal(String symbol, double price) {
        String message = "🟢 BUY SIGNAL: " + symbol + " at " + price;
        notificationService.send(message);
    }
}