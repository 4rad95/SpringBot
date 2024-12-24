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
    private long startDateTime;
    private long duration;
    private String symbols;
    @Enumerated(EnumType.STRING)
    private Type type;

}
