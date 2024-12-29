package org.binance.springbot.service;


import lombok.RequiredArgsConstructor;
import org.binance.springbot.dto.LogUpdateDto;
import org.binance.springbot.entity.LogUpdate;
import org.binance.springbot.mapper.Mappers;
import org.binance.springbot.repo.LogUpdateRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogUpdateService {

    private final Mappers mappers;
    private final ModelMapper modelMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private LogUpdateRepository logUpdateRepository;


    public void insertLogUpdate(LogUpdateDto logUpdateDto) {
        if (logUpdateDto.getMsg() != null)
        {
            mappers.convertToLogUpdateDto(logUpdateRepository.save(mappers.convertToLogUpdate(logUpdateDto)));
        }
    }
}
