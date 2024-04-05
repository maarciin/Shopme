package com.shopme.shipping;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.ShippingRate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ShippingRateRepositoryTests {

    @Autowired
    private ShippingRateRepository repo;

    @Autowired
    private TestEntityManager entityManager;

    private Country testCountry;

    @BeforeEach
    public void setUp() {
        testCountry = new Country();
        testCountry.setName("Test Country");
        testCountry.setCode("TC");
        entityManager.persist(testCountry);
    }

    @Test
    @DisplayName("Should find shipping rate by country and state")
    public void shouldFindShippingRateByCountryAndState() {
        String testState = "Test State";
        ShippingRate testRate = new ShippingRate();
        testRate.setCountry(testCountry);
        testRate.setState(testState);
        entityManager.persist(testRate);

        Optional<ShippingRate> foundRate = repo.findByCountryAndState(testCountry, testState);

        assertThat(foundRate).isPresent();
        assertThat(foundRate.get().getCountry()).isEqualTo(testCountry);
        assertThat(foundRate.get().getState()).isEqualTo(testState);
    }

    @Test
    @DisplayName("Should not find shipping rate by non-existing country and state")
    public void shouldNotFindShippingRateByNonExistingCountryAndState() {
        Country nonExistingCountry = new Country();
        nonExistingCountry.setName("Non-existing Country");
        nonExistingCountry.setCode("NC");
        entityManager.persist(nonExistingCountry);

        String nonExistingState = "Non-existing State";

        Optional<ShippingRate> foundRate = repo.findByCountryAndState(nonExistingCountry, nonExistingState);

        assertThat(foundRate).isNotPresent();
    }
}