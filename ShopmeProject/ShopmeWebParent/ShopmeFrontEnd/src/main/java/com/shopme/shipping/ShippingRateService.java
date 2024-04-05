package com.shopme.shipping;

import com.shopme.common.entity.Address;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.ShippingRate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class for handling shipping rates.
 */
@Service
@RequiredArgsConstructor
public class ShippingRateService {

    private final ShippingRateRepository shippingRateRepository;

    /**
     * Retrieves the shipping rate for a given customer.
     *
     * @param customer The customer for whom the shipping rate is to be retrieved.
     * @return The shipping rate for the customer.
     * @throws ShippingRateNotFoundException If no shipping rate is found for the customer's state.
     */
    public ShippingRate getShippingRateForCustomer(Customer customer) throws ShippingRateNotFoundException {
        String state = customer.getState();
        if (state == null || state.isEmpty()) {
            state = customer.getCity();
        }
        return shippingRateRepository.findByCountryAndState(customer.getCountry(), state)
                .orElseThrow(() -> new ShippingRateNotFoundException("No shipping rate found for the customer's state"));
    }

    /**
     * Retrieves the shipping rate for a given address.
     *
     * @param address The address for which the shipping rate is to be retrieved.
     * @return The shipping rate for the address.
     * @throws ShippingRateNotFoundException If no shipping rate is found for the address's state.
     */
    public ShippingRate getShippingRateForAddress(Address address) throws ShippingRateNotFoundException {
        String state = Optional.ofNullable(address.getState()).orElse(address.getCity());
        return shippingRateRepository.findByCountryAndState(address.getCountry(), state)
                .orElseThrow(() -> new ShippingRateNotFoundException("No shipping rate found for the address's state"));
    }
}