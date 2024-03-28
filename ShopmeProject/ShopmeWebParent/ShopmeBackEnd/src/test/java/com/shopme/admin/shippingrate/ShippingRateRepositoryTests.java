package com.shopme.admin.shippingrate;

import com.shopme.common.entity.ShippingRate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
class ShippingRateRepositoryTests {

    @Autowired
    private ShippingRateRepository shippingRateRepository;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testFindByCountryAndState() {
        Integer countryId = 180;
        String state = "Silesia";
        Optional<ShippingRate> rate = shippingRateRepository.findByCountryAndState(countryId, state);

        assertThat(rate).isPresent();
        assertThat(rate.get().getState()).isEqualTo(state);
    }

    @Test
    public void testUpdateCODSupport() {
        Integer rateId = 1;
        shippingRateRepository.updateCODSupport(rateId, false);

        ShippingRate rate = entityManager.find(ShippingRate.class, rateId);
        assertThat(rate.isCodSupported()).isFalse();
    }
}
