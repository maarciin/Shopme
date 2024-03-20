package com.shopme.setting;

import com.shopme.common.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, Integer> {

    List<Country> findAllByOrderByName();

    Optional<Country> findByCode(String code);


}
