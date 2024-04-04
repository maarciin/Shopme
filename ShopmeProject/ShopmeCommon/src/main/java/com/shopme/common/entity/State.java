package com.shopme.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "states")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class State extends IdBasedEntity {

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

    public State(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
