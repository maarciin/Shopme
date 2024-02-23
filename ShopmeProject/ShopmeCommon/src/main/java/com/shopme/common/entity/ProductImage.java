package com.shopme.common.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public ProductImage(String name, Product product) {
        this.name = name;
        this.product = product;
    }

    @Transient
    public String getImagePath() {
        return String.format("/product-images/%d/extras/%s", product.getId(), this.name);
    }
}
