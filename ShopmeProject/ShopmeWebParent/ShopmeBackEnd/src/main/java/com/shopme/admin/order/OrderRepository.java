package com.shopme.admin.order;

import com.shopme.admin.paging.SearchRepository;
import com.shopme.common.entity.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends SearchRepository<Order, Integer>, JpaRepository<Order, Integer> {

    @Query("SELECT o FROM Order o WHERE "
            + "CONCAT('#', o.id) LIKE %?1% OR "
            + "CONCAT(o.firstName, ' ', o.lastName) LIKE %?1% OR "
            + "o.firstName LIKE %?1% OR "
            + "o.lastName LIKE %?1% OR "
            + "o.addressLine1 LIKE %?1% OR "
            + "o.addressLine2 LIKE %?1% OR "
            + "o.city LIKE %?1% OR "
            + "o.state LIKE %?1% OR "
            + "o.postalCode LIKE %?1% OR "
            + "o.country LIKE %?1% OR "
            + "CAST(o.paymentMethod AS STRING) LIKE %?1% OR "
            + "CAST(o.status AS STRING) LIKE %?1%")
    Page<Order> findAll(String keyword, Pageable pageable);
}
