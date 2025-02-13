package org.binance.springbot.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SymbolsDto {
  //  private long symbolsId;
    private String symbols;
    private String lowBuy;
    private String highBuy;
    private String imbBuy;
    private String lowSell;
    private String highSell;
    private String imbSell;
}
