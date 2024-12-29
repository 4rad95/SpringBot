package org.binance.springbot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "LogUpdate")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LogUpdate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "id")
    private Long id;
    @Column (name = "time")
    private Timestamp time;
    @Column (name = "msg")
    private String msg;
}
