package com.shopme.admin.brand;

import com.shopme.common.entity.Brand;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BrandRepository extends PagingAndSortingRepository<Brand, Integer>, CrudRepository<Brand, Integer> {

    Long countById(Integer id);
}
