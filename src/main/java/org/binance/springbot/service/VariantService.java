package org.binance.springbot.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.binance.springbot.dto.SymbolsDto;
import org.binance.springbot.dto.VariantDto;
import org.binance.springbot.mapper.Mappers;
import org.binance.springbot.repo.VariantRepository;
import org.binance.springbot.util.MapperUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VariantService {
    private final VariantRepository variantRepository;
    private final Mappers mappers;
    private final ModelMapper modelMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    public void insertVariant(VariantDto variantDto) {
        if (variantDto.getSymbol() != null)
        {
            mappers.convertToVariantDto(variantRepository.save(mappers.convertToVariant(variantDto)));
        }
    }

    @Transactional
    public void deleteAllAndResetSequence() {
        variantRepository.deleteAll();
        jdbcTemplate.execute("ALTER TABLE variant ALTER COLUMN id RESTART WITH 1;");
    }
}
