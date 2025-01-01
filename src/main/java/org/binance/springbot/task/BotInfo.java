package org.binance.springbot.task;

import org.binance.springbot.SpringBotApplication;

import java.util.Date;

import static java.lang.System.currentTimeMillis;
import static org.binance.springbot.SpringBotApplication.timer;
import static org.binance.springbot.util.BinanceUtil.printBalance;

public class BotInfo {
    private String startBalance;
    private String currentBalance;
    private String startTime;
    private String duration;

    public BotInfo() {
        this.startBalance = SpringBotApplication.startBalance.toString();
        this.currentBalance = printBalance().toString();
        this.startTime = String.valueOf(new Date(timer));
        Long t0 = currentTimeMillis();
        int seconds = (int) ((t0 - SpringBotApplication.timer) / 1000);
        int minutes = seconds / 60;
        int hours = minutes / 60;
        minutes = minutes - hours * 60;
        seconds = seconds - minutes * 60;
        String formattedTime = String.format("%d:%02d:%02d", hours, minutes, seconds);
        this.duration = formattedTime;
    }

    public String getStartBalance() {
        return startBalance;
    }

    public String getCurrentBalance() {
        return currentBalance;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getDuration() {
        return duration;
    }
}
