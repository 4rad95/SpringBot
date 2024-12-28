package org.binance.springbot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import lombok.*;

 import java.sql.Date;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class OpenPositionDto {

    private Long id;
    private Timestamp time;
    private String symbol;
    private String type;
    private Long idBinance;
    private Long stopId;
    private Long profitId;
}