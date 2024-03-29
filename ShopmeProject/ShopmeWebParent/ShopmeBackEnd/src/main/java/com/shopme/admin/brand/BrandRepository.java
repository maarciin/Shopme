package com.shopme.admin.brand;

import com.shopme.admin.paging.SearchRepository;
import com.shopme.common.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface BrandRepository extends SearchRepository<Brand, Integer>, CrudRepository<Brand, Integer> {

    Long countById(Integer id);

    Optional<Brand> findByName(String name);

    @Query("SELECT b FROM Brand b WHERE b.name like %?1%")
    Page<Brand> findAll(String keyword, Pageable pageable);

    @Query("SELECT NEW Brand(b.id, b.name) FROM Brand b ORDER BY b.name ASC")
    List<Brand> findAll();
}
