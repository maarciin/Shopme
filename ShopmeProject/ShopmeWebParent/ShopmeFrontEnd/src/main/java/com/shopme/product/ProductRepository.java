package com.shopme.product;

import com.shopme.common.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProductRepository extends PagingAndSortingRepository<Product, Integer>, CrudRepository<Product, Integer> {

    @Query("SELECT p FROM Product p WHERE p.enabled = true AND " +
            "(p.category.id = ?1 OR p.category.allParentIds LIKE CONCAT('%-', ?1, '-%'))" +
            " ORDER BY p.name")
    Page<Product> listByCategory(Integer categoryId, Pageable pageable);
}
