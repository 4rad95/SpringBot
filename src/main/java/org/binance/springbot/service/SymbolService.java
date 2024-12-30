package org.binance.springbot.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.binance.springbot.dto.SymbolsDto;
import org.binance.springbot.entity.Symbols;
import org.binance.springbot.mapper.Mappers;
import org.binance.springbot.repo.SymbolsRepository;
import org.binance.springbot.util.MapperUtil;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SymbolService {
    private final SymbolsRepository symbolsRepository;
    private final Mappers mappers;
    private final ModelMapper modelMapper;

    public List<SymbolsDto> getAll() {
        return MapperUtil.convertList(symbolsRepository.findAll(), mappers::convertToSymbolsDto);
    }

    public void insertSymbols(SymbolsDto symbolsDto) {
        if (symbolsDto.getSymbols() != null)
               {
              mappers.convertToSymbolsDto(symbolsRepository.save(mappers.convertToSymbols(symbolsDto)));
        }
    }
    public SymbolsDto getSymbol(String symbols) {
        Symbols entity = symbolsRepository.findBySymbols(symbols); // Получаем одну сущность

        if (entity == null) {
            throw new EntityNotFoundException("Symbol not found: " + symbols);
        }

        return mappers.convertToSymbolsDto(entity); // Конвертируем в DTO и возвращаем
    }
    @Transactional
    public void deleteBySymbol(String symbol){
        symbolsRepository.deleteBySymbols(symbol);
    }

    @Transactional
    public void deleteAll(){
        symbolsRepository.deleteAll();
    }
}
