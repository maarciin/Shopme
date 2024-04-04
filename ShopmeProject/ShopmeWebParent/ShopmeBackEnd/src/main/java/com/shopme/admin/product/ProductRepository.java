package com.shopme.admin.product;

import com.shopme.common.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface ProductRepository extends PagingAndSortingRepository<Product, Integer>, CrudRepository<Product, Integer> {

    Optional<Product> findByName(String name);

    @Query("UPDATE Product p SET p.enabled = ?2 WHERE p.id = ?1")
    @Modifying
    void updateEnabledStatus(Integer id, boolean enabled);

    Long countById(Integer id);

    @Query("SELECT p FROM Product p WHERE " +
            "CONCAT(p.name, ' ', p.shortDescription, ' ', p.fullDescription, ' ',p.brand.name, ' ',p.category.name) LIKE %?1%")
    Page<Product> findAll(String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.category.id = ?1 OR p.category.allParentIds LIKE CONCAT('%-', ?1, '-%')")
    Page<Product> findAllInCategory(Integer categoryId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE (p.category.id = ?1 OR p.category.allParentIds LIKE CONCAT('%-', ?1, '-%'))" +
            "AND " +
            "CONCAT(p.name, ' ', p.shortDescription, ' ', p.fullDescription, ' ',p.brand.name, ' ',p.category.name) LIKE %?2%")
    Page<Product> searchInCategory(Integer categoryId, String keyword, Pageable pageable);
}
