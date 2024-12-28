package org.binance.springbot.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.binance.springbot.entity.enums.Type;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatisticDto {
    private long id;
    private String startDateTime;
    private String duration;
    private String symbols;
    @Enumerated(EnumType.STRING)
    private Type type;
    private String pnl;
    private String comission;
}

