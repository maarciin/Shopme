package com.shopme.common.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "states")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class State {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 45)
    private String name;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    public State(String name, Country country) {
        this.name = name;
        this.country = country;
    }
}
