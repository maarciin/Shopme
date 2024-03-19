package com.shopme.customer;

import com.shopme.common.entity.AuthenticationType;
import com.shopme.common.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    Optional<Customer> findByEmail(String email);

    Optional<Customer> findByVerificationCode(String verificationCode);

    @Query("UPDATE Customer c SET c.enabled = true, c.verificationCode = null WHERE c.id = ?1")
    @Modifying
    void enableCustomer(Integer id);


    @Query("UPDATE Customer c SET c.authenticationType = ?2 WHERE c.id = ?1")
    @Modifying
    void updateAuthenticationType(Integer customerId, AuthenticationType type);
}
