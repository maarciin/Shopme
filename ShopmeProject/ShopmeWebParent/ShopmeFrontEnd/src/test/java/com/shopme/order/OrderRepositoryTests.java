package com.shopme.order;

import com.shopme.common.entity.order.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class OrderRepositoryTests {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    public void testFindAll() {
        Integer customerId = 37;
        Page<Order> page = orderRepository.findAll(customerId, PageRequest.of(0, 6));

        page.forEach(System.out::println);

        assertThat(page).isNotNull();
        assertThat(page.getContent().size()).isGreaterThan(0);
    }


}