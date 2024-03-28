package com.shopme.admin.shippingrate;

import com.shopme.admin.paging.SearchRepository;
import com.shopme.common.entity.ShippingRate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * Repository interface for managing shipping rates.
 * It extends JpaRepository and SearchRepository to provide CRUD operations and search functionality.
 */
public interface ShippingRateRepository extends SearchRepository<ShippingRate, Integer>, JpaRepository<ShippingRate, Integer> {

    /**
     * Finds all shipping rates that match the given keyword.
     * The keyword is used to search in the country name and state of the shipping rates.
     *
     * @param keyword  The keyword to search for.
     * @param pageable The pagination information.
     * @return A page of shipping rates that match the keyword.
     */
    @Query("SELECT sr FROM ShippingRate sr WHERE CONCAT(sr.country.name, ' ', sr.state) LIKE %?1%")
    Page<ShippingRate> findAll(String keyword, Pageable pageable);

    /**
     * Updates the COD support status of a shipping rate.
     *
     * @param id           The ID of the shipping rate to update.
     * @param codSupported The new COD support status.
     */
    @Modifying
    @Query("UPDATE ShippingRate sr SET sr.codSupported = ?2 WHERE sr.id = ?1")
    void updateCODSupport(Integer id, boolean codSupported);

    /**
     * Finds a shipping rate by country ID and state.
     *
     * @param id    The ID of the country.
     * @param state The state.
     * @return An Optional containing the shipping rate if found, or empty if not found.
     */
    @Query("SELECT sr FROM ShippingRate sr WHERE sr.country.id = ?1 AND sr.state = ?2")
    Optional<ShippingRate> findByCountryAndState(Integer id, String state);
}