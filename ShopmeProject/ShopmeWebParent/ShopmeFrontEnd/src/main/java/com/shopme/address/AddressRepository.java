package com.shopme.address;

import com.shopme.common.entity.Address;
import com.shopme.common.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Integer> {

    List<Address> findByCustomer(Customer customer);

    @Query("SELECT a FROM Address a WHERE a.id = ?1 AND a.customer.id = ?2")
    Optional<Address> findByIdAndCustomer(Integer id, Integer customerId);

    @Modifying
    @Query("DELETE FROM Address a WHERE a.id = ?1 AND a.customer.id = ?2")
    void deleteByIdAndCustomer(Integer id, Integer customerId);
}
