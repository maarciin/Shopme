package com.shopme.shoppingcart;

import com.shopme.Utility;
import com.shopme.address.AddressNotFoundException;
import com.shopme.address.AddressService;
import com.shopme.common.entity.Address;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.ShippingRate;
import com.shopme.customer.CustomerService;
import com.shopme.shipping.ShippingRateNotFoundException;
import com.shopme.shipping.ShippingRateService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * This class is a controller that handles shopping cart related requests.
 */
@Controller
@RequiredArgsConstructor
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;
    private final CustomerService customerService;
    private final AddressService addressService;
    private final ShippingRateService shippingRateService;

    /**
     * This method handles a GET request to view the cart.
     *
     * @param model   The Model object.
     * @param request The HttpServletRequest object.
     * @return A string representing the name of the view.
     */
    @GetMapping("/cart")
    public String viewCart(Model model, HttpServletRequest request) {
        var customer = getAuthenticatedCustomer(request);
        var cartItems = shoppingCartService.listCartItems(customer);
        var estimatedTotal = cartItems.stream()
                .reduce(0.0f, (subtotal, cartItem) -> subtotal + cartItem.getSubtotal(), Float::sum);
        Address defaultAddress = null;
        ShippingRate shippingRate = null;
        boolean usePrimaryAddressAsDefault = false;

        try {
            defaultAddress = addressService.getDefaultAddress(customer);
            shippingRate = shippingRateService.getShippingRateForAddress(defaultAddress);
        } catch (AddressNotFoundException e) {
            usePrimaryAddressAsDefault = true;
            try {
                shippingRate = shippingRateService.getShippingRateForCustomer(customer);
            } catch (ShippingRateNotFoundException ex) {
            }
        } catch (ShippingRateNotFoundException e) {
        }


        model.addAttribute("usePrimaryAddressAsDefault", usePrimaryAddressAsDefault);
        model.addAttribute("shippingSupported", shippingRate != null);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("estimatedTotal", estimatedTotal);
        return "cart/shopping_cart";
    }

    /**
     * This method retrieves the authenticated customer from the request.
     *
     * @param request The HttpServletRequest object.
     * @return The authenticated Customer object.
     */
    private Customer getAuthenticatedCustomer(HttpServletRequest request) {
        var email = Utility.getEmailOfAuthenticatedCustomer(request);
        return customerService.getCustomerByEmail(email);
    }
}