package org.binance.springbot.sheduled;

import org.binance.springbot.dto.SymbolsDto;
import org.binance.springbot.repo.SymbolsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.binance.springbot.service.SymbolService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import static org.binance.springbot.util.BinanceUtil.getBitcoinSymbols;

@Service
public class SheduledService {

    @Autowired
    private SymbolsRepository symbolsRepository;
    @Autowired
    private SymbolService symbolService;

    //    @Scheduled(fixedRateString = "${fixedDelay.in.milliseconds}")
    @Scheduled(cron = "${cron.expression}")
    @Async
    public void scheduleFixedRateTask() throws Exception {
//        System.out.println(
//                "Fixed rate task - " + System.currentTimeMillis() / 1000);
//        List<String> symbols = getBitcoinSymbols();
//        for (String str:symbols) {
//            SymbolsDto symbolDto = SymbolsDto.builder().symbols(str).build();
//            symbolDto.setSymbolsId(0L);
//            symbolService.insertSymbols(symbolDto);
//        }

    }
}