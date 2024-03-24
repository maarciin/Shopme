package com.shopme.shoppingcart;

import com.shopme.Utility;
import com.shopme.common.entity.Customer;
import com.shopme.customer.CustomerNotFoundException;
import com.shopme.customer.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class is a REST controller that handles shopping cart related requests.
 */
@RestController
@RequiredArgsConstructor
public class ShoppingCartRestController {

    private final ShoppingCartService shoppingCartService;
    private final CustomerService customerService;

    /**
     * This method handles a POST request to add a product to the cart.
     *
     * @param productId The ID of the product to be added to the cart.
     * @param quantity  The quantity of the product to be added to the cart.
     * @param request   The HttpServletRequest object.
     * @return A string message indicating the result of the operation.
     */
    @PostMapping("/cart/add/{productId}/{quantity}")
    public String addProductToCart(@PathVariable Integer productId, @PathVariable Integer quantity, HttpServletRequest request) {
        try {
            Customer customer = getAuthenticatedCustomer(request);
            Integer updatedQuantity = shoppingCartService.addProduct(productId, quantity, customer);

            return updatedQuantity + " item(s) of this product were added to your cart";
        } catch (CustomerNotFoundException e) {
            return "You need to login to add products to your cart";
        } catch (ShoppingCartException e) {
            return e.getMessage();
        }

    }

    /**
     * This method retrieves the authenticated customer from the request.
     *
     * @param request The HttpServletRequest object.
     * @return The authenticated Customer object.
     * @throws CustomerNotFoundException If no authenticated customer is found.
     */
    private Customer getAuthenticatedCustomer(HttpServletRequest request) throws CustomerNotFoundException {
        String email = Utility.getEmailOfAuthenticatedCustomer(request);
        if (email == null) {
            throw new CustomerNotFoundException("No authenticated customer");
        }
        return customerService.getCustomerByEmail(email);
    }

}