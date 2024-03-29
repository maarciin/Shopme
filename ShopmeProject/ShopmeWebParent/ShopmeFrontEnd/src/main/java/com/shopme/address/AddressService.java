package com.shopme.address;

import com.shopme.common.entity.Address;
import com.shopme.common.entity.Customer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for managing customer addresses.
 * It handles the business logic related to the address book.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AddressService {

    private final AddressRepository addressRepository;

    /**
     * Retrieves a list of addresses for a given customer.
     *
     * @param customer The customer whose addresses are to be retrieved.
     * @return A list of addresses belonging to the given customer.
     */
    public List<Address> listAddressBook(Customer customer) {
        return addressRepository.findByCustomer(customer);
    }

    /**
     * Saves the given address to the database.
     *
     * @param address The address to be saved.
     */
    public void save(Address address) {
        addressRepository.save(address);
    }

    /**
     * Retrieves an address with the given ID for a given customer.
     *
     * @param id         The ID of the address to be retrieved.
     * @param customerId The ID of the customer whose address is to be retrieved.
     * @return The address with the given ID for the given customer.
     * @throws AddressNotFoundException if no address with the given ID for the given customer could be found.
     */
    public Address get(Integer id, Integer customerId) throws AddressNotFoundException {
        return addressRepository.findByIdAndCustomer(id, customerId)
                .orElseThrow(() -> new AddressNotFoundException("Could not find any address with ID " + id));
    }

    /**
     * Deletes an address with the given ID for a given customer.
     *
     * @param addressId  The ID of the address to be deleted.
     * @param customerId The ID of the customer whose address is to be deleted.
     */
    public void delete(Integer addressId, Integer customerId) {
        addressRepository.deleteByIdAndCustomer(addressId, customerId);
    }

    /**
     * Sets the given address as the default address for a given customer.
     *
     * @param defaultAddressId The ID of the address to be set as the default.
     * @param customerId       The ID of the customer whose address is to be set as the default.
     */
    public void setDefaultAddress(Integer defaultAddressId, Integer customerId) {
        if (defaultAddressId > 0) {
            addressRepository.setDefaultAddress(defaultAddressId);
        }
        addressRepository.setNonDefaultForOthers(defaultAddressId, customerId);
    }

}