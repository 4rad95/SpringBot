package org.binance.springbot.service;

import lombok.RequiredArgsConstructor;
import org.binance.springbot.dto.OpenPositionDto;
import org.binance.springbot.dto.VariantDto;
import org.binance.springbot.mapper.Mappers;
import org.binance.springbot.repo.OpenPositionRepository;
import org.binance.springbot.repo.VariantRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OpenPositionService {
    private final VariantRepository variantRepository;
    private final Mappers mappers;
    private final ModelMapper modelMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private OpenPositionRepository openPositionRepository;


    public void insertOpenPosition(OpenPositionDto openPositionDto) {
        if (openPositionDto.getSymbol() != null)
        {
            mappers.convertToOpenPositionDto(openPositionRepository.save(mappers.convertToOpenPosition(openPositionDto)));
        }
    }
    public boolean getOpenPositionSymbol(String symbol) {
        if (openPositionRepository.getOpenPositionSymbol(symbol) >0) {return true;}
        return false;
    }

    public Integer getCount() {
       return openPositionRepository.getCount();
    }
    @Transactional
     public void deleteAllAndResetSequence() {
        variantRepository.deleteAll();
        jdbcTemplate.execute("ALTER TABLE variant ALTER COLUMN id RESTART WITH 1;");
    }
}