package com.shopme.admin.setting.state;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.State;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class StateRepositoryTests {

    @Autowired private StateRepository repo;
    @Autowired private TestEntityManager entityManager;

    @Test
    public void testCreateStatesInIndia() {
        Integer countryId = 2;
        Country country = entityManager.find(Country.class, countryId);

//		State state = repo.save(new State("Karnataka", country));
//		State state = repo.save(new State("Punjab", country));
//		State state = repo.save(new State("Uttar Pradesh", country));
        State state = repo.save(new State("West Bengal", country));

        assertThat(state).isNotNull();
        assertThat(state.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateStatesInUS() {
        Integer countryId = 3;
        Country country = entityManager.find(Country.class, countryId);

//		State state = repo.save(new State("California", country));
//		State state = repo.save(new State("Texas", country));
//		State state = repo.save(new State("New York", country));
        State state = repo.save(new State("Washington", country));

        assertThat(state).isNotNull();
        assertThat(state.getId()).isGreaterThan(0);
    }

    @Test
    public void testListStatesByCountry() {
        Integer countryId = 2;
        Country country = entityManager.find(Country.class, countryId);
        List<State> listStates = repo.findByCountryOrderByName(country);

        listStates.forEach(System.out::println);

        assertThat(listStates.size()).isGreaterThan(0);
    }



}