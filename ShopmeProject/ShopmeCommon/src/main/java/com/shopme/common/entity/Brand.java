package com.shopme.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "brands")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 45, unique = true)
    private String name;

    @Column(nullable = false, length = 128)
    private String logo;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "brand_categories",
            joinColumns = @JoinColumn(name = "brand_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

    public Brand(String name, String logo) {
        this.name = name;
        this.logo = logo;
    }

    @Transient
    public String getLogoPath() {
        if(this.id == null) {
            return "/images/image-thumbnail.png";
        }
        return "/brand-logos/" + this.id + "/" + this.logo;
    }
}
