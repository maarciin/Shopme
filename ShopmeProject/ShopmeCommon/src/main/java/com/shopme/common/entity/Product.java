package com.shopme.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "products")
@NoArgsConstructor
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 256, unique = true, nullable = false)
    private String name;

    @Column(length = 256, unique = true, nullable = false)
    private String alias;

    @Column(length = 512, nullable = false)
    private String shortDescription;

    @Column(length = 4096, nullable = false)
    private String fullDescription;

    private Date createdTime;

    private Date updatedTime;

    private boolean enabled;
    private boolean inStock;

    private float cost;

    private float price;
    private float discountPercent;

    private float length;
    private float width;
    private float height;
    private float weight;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", shortDescription='" + shortDescription + '\'' +
                '}';
    }
}