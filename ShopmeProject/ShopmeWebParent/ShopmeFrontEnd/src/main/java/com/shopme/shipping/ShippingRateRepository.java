package com.shopme.shipping;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.ShippingRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShippingRateRepository extends JpaRepository<ShippingRate, Integer>{

    Optional<ShippingRate> findByCountryAndState(Country country, String state);
}
