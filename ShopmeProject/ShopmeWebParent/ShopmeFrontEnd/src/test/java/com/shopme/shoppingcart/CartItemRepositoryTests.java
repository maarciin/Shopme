package com.shopme.shoppingcart;

import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.product.Product;
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
public class CartItemRepositoryTests {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testSaveItem() {
        Integer customerId = 1;
        Integer productId = 1;

        Customer customer = entityManager.find(Customer.class, customerId);
        Product product = entityManager.find(Product.class, productId);

        CartItem cartItem = new CartItem();
        cartItem.setCustomer(customer);
        cartItem.setProduct(product);
        cartItem.setQuantity(1);

        CartItem savedItem = cartItemRepository.save(cartItem);

        assertThat(savedItem.getId()).isGreaterThan(0);

    }

    @Test
    public void testSave2Items() {
        Integer customerId = 10;
        Integer productId = 10;

        Customer customer = entityManager.find(Customer.class, customerId);
        Product product = entityManager.find(Product.class, productId);

        CartItem cartItem = new CartItem();
        cartItem.setCustomer(customer);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);

        CartItem cartItem2 = new CartItem();
        cartItem2.setCustomer(new Customer(8));
        cartItem2.setProduct(new Product(8));
        cartItem2.setQuantity(3);

        List<CartItem> cartItems = cartItemRepository.saveAll(List.of(cartItem, cartItem2));

        assertThat(cartItems.size()).isEqualTo(2);

    }

    @Test
    public void testFindByCustomer() {
        Integer customerId = 1;

        List<CartItem> cartItems = cartItemRepository.findByCustomer(new Customer(customerId));

        assertThat(cartItems.size()).isEqualTo(1);

    }

    @Test
    public void testFindByCustomerAndProduct() {
        Integer customerId = 1;
        Integer productId = 1;

        Optional<CartItem> cartItem =
                cartItemRepository.findByCustomerAndProduct(new Customer(customerId), new Product(productId));

        assertThat(cartItem).isPresent();
    }

    @Test
    public void testUpdateQuantity() {
        Integer customerId = 1;
        Integer productId = 1;
        Integer quantity = 5;

        cartItemRepository.updateQuantity(quantity, customerId, productId);

        CartItem cartItem = cartItemRepository.findByCustomerAndProduct(new Customer(customerId), new Product(productId)).get();

        assertThat(cartItem.getQuantity()).isEqualTo(quantity);
    }

    @Test
    public void testDeleteByCustomerAndProduct() {
        Integer customerId = 1;
        Integer productId = 1;

        cartItemRepository.deleteByCustomerAndProduct(customerId, productId);

        Optional<CartItem> cartItem = cartItemRepository.findByCustomerAndProduct(new Customer(customerId), new Product(productId));

        assertThat(cartItem).isNotPresent();
    }
}