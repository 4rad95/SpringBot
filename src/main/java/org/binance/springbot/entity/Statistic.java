package entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Cart")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Statistic {
    @Id
    @Column (name = id)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long startDateTime;
    private long duration;
    private String symbol;
    private 

}
