package com.shopme.shoppingcart;

import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Product;
import com.shopme.product.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * This service class is responsible for managing the shopping cart.
 */
@Service
@RequiredArgsConstructor
public class ShoppingCartService {

    private static final int CART_MAX_QUANTITY = 5;

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    /**
     * Adds a product to the shopping cart of a given customer.
     * If the product is already in the cart, the quantity is updated.
     * If the quantity exceeds the maximum allowed quantity, an exception is thrown.
     *
     * @param productId the ID of the product to add
     * @param quantity  the quantity of the product to add
     * @param customer  the customer who owns the shopping cart
     * @return the updated quantity of the product in the cart
     * @throws ShoppingCartException if the quantity exceeds the maximum allowed quantity
     */
    public Integer addProduct(Integer productId, Integer quantity, Customer customer) throws ShoppingCartException {
        Product product = new Product(productId);
        CartItem cartItem = getCartItem(customer, product);
        Integer updatedQuantity = cartItem.getQuantity() + quantity;
        if (updatedQuantity > CART_MAX_QUANTITY) {
            throw new ShoppingCartException("Could not add more " + quantity + " item(s)"
                    + " because there's already " + cartItem.getQuantity() + " item(s) "
                    + "in your shopping cart. Maximum allowed quantity is " + CART_MAX_QUANTITY + ".");
        }
        cartItem.setQuantity(updatedQuantity);
        cartItemRepository.save(cartItem);
        return updatedQuantity;
    }

    /**
     * Retrieves a cart item for a given customer and product.
     * If no cart item exists, a new one is created.
     *
     * @param customer the customer who owns the shopping cart
     * @param product  the product to find in the cart
     * @return the cart item for the given customer and product
     */
    private CartItem getCartItem(Customer customer, Product product) {
        return cartItemRepository.findByCustomerAndProduct(customer, product)
                .orElseGet(() -> createNewCartItem(customer, product));
    }

    /**
     * Creates a new cart item for a given customer and product.
     * The initial quantity of the product in the cart is set to 0.
     *
     * @param customer the customer who will own the new cart item
     * @param product  the product to add to the new cart item
     * @return the newly created cart item
     */
    private CartItem createNewCartItem(Customer customer, Product product) {
        CartItem cartItem = new CartItem();
        cartItem.setCustomer(customer);
        cartItem.setProduct(product);
        cartItem.setQuantity(0);
        return cartItem;
    }

    /**
     * Retrieves all cart items for a given customer.
     *
     * @param customer the customer who owns the shopping cart
     * @return a list of cart items for the given customer
     */
    public List<CartItem> listCartItems(Customer customer) {
        return cartItemRepository.findByCustomer(customer);
    }

    /**
     * Updates the quantity of a product in the shopping cart of a given customer.
     *
     * @param productId the ID of the product to update
     * @param quantity  the new quantity of the product
     * @param customer  the customer who owns the shopping cart
     * @return the updated subtotal of the product in the cart
     */
    @Transactional
    public float updateQuantity(Integer productId, Integer quantity, Customer customer) {
        cartItemRepository.updateQuantity(quantity, customer.getId(), productId);
        return productRepository.findById(productId)
                .map(product -> product.getDiscountPrice() * quantity)
                .orElse(0.0f);
    }
}
