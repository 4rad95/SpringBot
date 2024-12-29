package org.binance.springbot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogUpdateDto {
    private Long id;
    private Timestamp time;
    private String msg;
}
