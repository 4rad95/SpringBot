package org.binance.springbot.mapper;


import lombok.RequiredArgsConstructor;
import org.binance.springbot.dto.*;
import org.binance.springbot.entity.*;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Mappers {
    private final ModelMapper modelMapper;

    public SymbolsDto convertToSymbolsDto(Symbols symbols) {return modelMapper.map(symbols, SymbolsDto.class);}

    public Symbols convertToSymbols(SymbolsDto symbolsDto) {
        return modelMapper.map(symbolsDto, Symbols.class);
    }

    public VariantDto convertToVariantDto(Variant variant) {return modelMapper.map(variant, VariantDto.class);}

    public Variant convertToVariant(VariantDto variantDto) {
        return modelMapper.map(variantDto, Variant.class);
    }

    public OpenPositionDto convertToOpenPositionDto(OpenPosition openPosition) {return modelMapper.map(openPosition, OpenPositionDto.class);}

    public OpenPosition convertToOpenPosition(OpenPositionDto openPositionDto) { return modelMapper.map(openPositionDto, OpenPosition.class);}

    public StatisticDto convertToStatisticDto(Statistic statistic) {return modelMapper.map(statistic, StatisticDto.class);}

    public Statistic convertToStatistic (StatisticDto statisticDto) { return modelMapper.map(statisticDto, Statistic.class);}

    public LogUpdateDto convertToLogUpdateDto(LogUpdate logUpdate) {return modelMapper.map(logUpdate, LogUpdateDto.class);}

    public LogUpdate convertToLogUpdate (LogUpdateDto logUpdateDto) { return modelMapper.map(logUpdateDto, LogUpdate.class);}

    public MonitorDto convertToMonitorDto(Monitor monitor) {return modelMapper.map(monitor, MonitorDto.class);}

    public Monitor convertToMonitor (MonitorDto monitorDto) { return modelMapper.map(monitorDto, Monitor.class);}
}
