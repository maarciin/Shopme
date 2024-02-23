package com.shopme.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product_details")
@Getter
@Setter
@NoArgsConstructor
public class ProductDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, length = 255)
    private String value;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public ProductDetail(String name, String value, Product product) {
        this.name = name;
        this.value = value;
        this.product = product;
    }

    public ProductDetail(Integer id, String name, String value, Product product) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.product = product;
    }
}
