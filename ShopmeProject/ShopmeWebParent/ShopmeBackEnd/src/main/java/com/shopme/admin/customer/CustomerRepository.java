package com.shopme.admin.customer;

import com.shopme.common.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CustomerRepository extends PagingAndSortingRepository<Customer, Integer>, JpaRepository<Customer, Integer> {
    Long countById(Integer id);
}
