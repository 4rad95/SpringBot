package org.binance.springbot.entity;

import jakarta.persistence.*;
import lombok.*;

import org.binance.springbot.entity.enums.Type;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Statistic")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Statistic {
    @Id
    @Column (name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column (name = "startDateTime")
    private String startDateTime;
    @Column (name = "duration")
    private String duration;
    @Column (name = "symbols")
    private String symbols;
    @Column (name = "type")
    @Enumerated(EnumType.STRING)
    private Type type;
    @Column (name = "pnl")
    private String pnl;
    @Column (name = "comission")
    private String comission;
}
