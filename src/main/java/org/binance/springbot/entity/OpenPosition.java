package org.binance.springbot.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "OpenPosition")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class OpenPosition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "id")
    private Long id;
    @Column (name = "time")
    private Timestamp time;
    @Column (name = "symbol")
    private String symbol;
    @Column (name = "type")
    private String type;
    @Column (name = "idBinance")
    private String idBinance;
    @Column (name = "stopId")
    private Long stopId;
    @Column (name = "profitId")
    private Long profitId;
}
