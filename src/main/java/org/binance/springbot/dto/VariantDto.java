package org.binance.springbot.dto;

import jakarta.persistence.Column;
import lombok.*;

import java.sql.Date;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class VariantDto {

    private Long id;
    private Timestamp time;
    private String symbol;
    private String type;
    private String price;
    private String enterPrice;
    private String stop;
    private String proffit;

}
