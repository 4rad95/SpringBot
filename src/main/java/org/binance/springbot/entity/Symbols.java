package org.binance.springbot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "Symbols")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Symbols {
//    @Id
//    @Column(name = "id")
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private long id;
    @Id
    @Column(name = "symbols")
    private String symbols;

    @Column(name = "lowbuy" )
    private String lowBuy;
    @Column(name = "highbuy" )
    private String highBuy;
    @Column(name = "imbbuy" )
    private String imbBuy;
    @Column(name = "lowsell" )
    private String lowSell;
    @Column(name = "highsell" )
    private String highSell;
    @Column(name = "imbsell" )
    private String imbSell;
}
