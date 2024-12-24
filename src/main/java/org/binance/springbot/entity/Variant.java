package org.binance.springbot.entity;

import jakarta.persistence.*;
import lombok.*;

import org.binance.springbot.entity.enums.Type;

import java.sql.Date;
import java.sql.Timestamp;


@Entity
@Table(name = "Variant")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Variant {
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
    @Column (name = "price")
    private String price;
    @Column (name = "enterprice")
    private String enterPrice;
    @Column (name = "stop")
    private String stop;
    @Column (name = "proffit")
    private String proffit;

}
