package org.binance.springbot.service;

import lombok.RequiredArgsConstructor;
import org.binance.springbot.dto.StatisticDto;
import org.binance.springbot.mapper.Mappers;
import org.binance.springbot.repo.OpenPositionRepository;
import org.binance.springbot.repo.StatisticRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatisticService {
    private final Mappers mappers;
    private final ModelMapper modelMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private StatisticRepository statisticRepository;

    public void insertStatistic(StatisticDto statisticDto) {
        if (statisticDto.getSymbols() != null)
        {
            mappers.convertToStatisticDto(statisticRepository.save(mappers.convertToStatistic(statisticDto)));
        }
    }
}
