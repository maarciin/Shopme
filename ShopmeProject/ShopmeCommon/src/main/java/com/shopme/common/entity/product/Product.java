package com.shopme.common.entity.product;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.IdBasedEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Entity
@Table(name = "products")
@NoArgsConstructor
@Getter
@Setter
public class Product extends IdBasedEntity {
    @Column(length = 256, unique = true, nullable = false)
    private String name;

    @Column(length = 256, unique = true, nullable = false)
    private String alias;

    @Column(length = 700, nullable = false)
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

    @Column(nullable = false)
    private String mainImage;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductImage> images = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductDetail> details = new ArrayList<>();

    public Product(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", shortDescription='" + shortDescription + '\'' +
                '}';
    }

    public void addExtraImage(String image) {
        this.images.add(new ProductImage(image, this));
    }

    @Transient
    public String getMainImagePath() {
        if (id == null || mainImage == null) return "/images/image-thumbnail.png";
        return "/product-images/" + this.id + "/" + this.mainImage;
    }

    public void addDetail(String name, String value) {
        this.details.add(new ProductDetail(name, value, this));
    }

    public void addDetail(Integer id, String name, String value) {
        this.details.add(new ProductDetail(id, name, value, this));
    }

    public boolean containsImageName(String imageName) {
        for (ProductImage image : images) {
            if (image.getName().equals(imageName)) {
                return true;
            }
        }
        return false;
    }

    @Transient
    public String getShortName() {
        if (name.length() > 70) {
            return name.substring(0, 70).concat("...");
        }
        return name;
    }

    @Transient
    public float getDiscountPrice() {
        if (discountPercent > 0) {
            return price * ((100 - discountPercent) / 100);
        }
        return this.price;
    }
}
