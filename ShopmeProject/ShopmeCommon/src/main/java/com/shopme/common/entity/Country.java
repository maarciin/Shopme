package com.shopme.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Entity
@Table(name = "countries")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Country extends IdBasedEntity {

    @Column(nullable = false, length = 45)
    private String name;

    @Column(nullable = false, length = 5)
    private String code;

    @ToString.Exclude
    @OneToMany(mappedBy = "country")
    @JsonIgnore
    private Set<State> states;

    public Country(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public Country(Integer id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }
}
