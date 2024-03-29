package com.shopme.address;

import com.shopme.common.entity.Address;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class AddressRepositoryTests {

    @Autowired
    private AddressRepository repo;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateAddress() {
        Country usa = entityManager.find(Country.class, 234);
        Customer customer = entityManager.find(Customer.class, 1);

        Address addressToSave = Address.builder()
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("1234567890")
                .addressLine1("123 Main Street")
                .city("New York")
                .state("NY")
                .postalCode("10001")
                .country(usa)
                .customer(customer)
                .defaultForShipping(true)
                .build();

        Address savedAddress = repo.save(addressToSave);

        assertThat(savedAddress.getId()).isGreaterThan(0);
    }

    @Test
    public void testFindByCustomer() {
        Customer customer = entityManager.find(Customer.class, 1);
        List<Address> addresses = repo.findByCustomer(customer);

        assertThat(addresses.size()).isGreaterThan(0);
        addresses.forEach(System.out::println);
    }

    @Test
    public void testFindByIdAndCustomer() {
        Integer addressId = 1;
        Integer customerId = 1;
        Optional<Address> address = repo.findByIdAndCustomer(addressId, customerId);
        assertThat(address).isPresent();
        System.out.println(address.get());
    }

    @Test
    public void testDeleteByIdAndCustomer() {
        Integer addressId = 1;
        Integer customerId = 1;
        repo.deleteByIdAndCustomer(addressId, customerId);
        Optional<Address> address = repo.findByIdAndCustomer(addressId, customerId);
        assertThat(address).isNotPresent();
    }

    @Test
    public void testSetDefaultAddress() {
        Integer addressId = 10;
        repo.setDefaultAddress(addressId);
        Address address = repo.findById(addressId).get();
        assertThat(address.isDefaultForShipping()).isTrue();
    }

    @Test
    public void testSetNonDefaultForOthers() {
        Integer addressId = 10;
        Integer customerId = 1;
        repo.setNonDefaultForOthers(addressId, customerId);
        List<Address> addresses = repo.findByCustomer(entityManager.find(Customer.class, customerId));
        addresses.forEach(address -> {
            if (address.getId() != addressId) {
                assertThat(address.isDefaultForShipping()).isFalse();
            }
        });
    }
}