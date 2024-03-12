package com.shopme.customer;

import com.shopme.common.entity.Country;
import com.shopme.setting.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CountryRepository countryRepository;

    public List<Country> listAllCountries() {
        return countryRepository.findAllByOrderByName();
    }

    public boolean isEmailUnique(String email) {
        return customerRepository.findByEmail(email).isEmpty();
    }

}
