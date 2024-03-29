package com.shopme.admin.order;

import com.shopme.common.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface OrderRepository extends PagingAndSortingRepository<Order, Integer>, JpaRepository<Order, Integer>{
}
