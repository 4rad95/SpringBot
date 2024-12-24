package org.binance.springbot.util;

import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.misc.LogManager;
import org.binance.springbot.dto.SymbolsDto;
import org.binance.springbot.mapper.Mappers;
import org.binance.springbot.repo.SymbolsRepository;
import org.binance.springbot.service.SymbolService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.binance.springbot.SpringBotApplication.symbolsRepository;
import static org.binance.springbot.util.BinanceUtil.getBitcoinSymbols;

@RequiredArgsConstructor
public class TableUtil {
    @Autowired
    private   SymbolService symbolService;
    @Autowired
    private  final SymbolsRepository symbolsRepository ;
    private final Mappers mappers;
    private final ModelMapper modelMapper;


    public void initSymbos() throws Exception {
        List<String> symbols = getBitcoinSymbols();
        for (String str:symbols) {
            SymbolsDto symbolDto = SymbolsDto.builder().symbols(str).build();
       //     symbolDto.setSymbolsId(0L);
            Mappers mappers = null;

            symbolsRepository.save(mappers.convertToSymbols(symbolDto));
            symbolService.insertSymbols(symbolDto);
        }

    }
}
