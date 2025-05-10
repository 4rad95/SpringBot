package org.binance.springbot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
@Entity
@Table(name = "Monitor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Monitor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "type")
    private String type;
    @Column(name = "symbol")
    private String symbol;
    @Column(name = "start")
    private String start;
    @Column(name = "stop")
    private String stop;
    @Column(name = "profit")
    private String profit;
}
