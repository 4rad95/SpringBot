package org.binance.springbot.mapper;


import lombok.RequiredArgsConstructor;
import org.binance.springbot.dto.OpenPositionDto;
import org.binance.springbot.dto.SymbolsDto;
import org.binance.springbot.dto.VariantDto;
import org.binance.springbot.entity.OpenPosition;
import org.binance.springbot.entity.Symbols;
import org.binance.springbot.entity.Variant;
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

    public OpenPosition convertToOpenPosition(OpenPositionDto openPositionDto) { return modelMapper.map(openPositionDto, OpenPosition.class);
    }

}
