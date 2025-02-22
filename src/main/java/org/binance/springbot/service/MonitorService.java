package org.binance.springbot.service;

import lombok.RequiredArgsConstructor;
import org.binance.springbot.dto.MonitorDto;
import org.binance.springbot.entity.Monitor;
import org.binance.springbot.entity.OpenPosition;
import org.binance.springbot.mapper.Mappers;
import org.binance.springbot.repo.MonitorRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class MonitorService {

    private final Mappers mappers;
    private final ModelMapper modelMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private MonitorRepository monitorRepository;

    public void insertMonitor(MonitorDto monitorDto) {
        if (monitorDto.getSymbol() != null)
        {
            mappers.convertToMonitorDto(monitorRepository.save(mappers.convertToMonitor(monitorDto)));
        }
    }

    public List<Monitor> getAll(){
        return monitorRepository.findAll();
    }

    public void deleteById(Long id) {
       monitorRepository.deleteById(id);
    }
}
