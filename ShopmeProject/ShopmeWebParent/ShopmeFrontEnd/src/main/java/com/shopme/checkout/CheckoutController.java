package com.shopme.checkout;

import com.shopme.Utility;
import com.shopme.address.AddressNotFoundException;
import com.shopme.address.AddressService;
import com.shopme.common.entity.Address;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.ShippingRate;
import com.shopme.common.entity.order.PaymentMethod;
import com.shopme.customer.CustomerService;
import com.shopme.order.OrderService;
import com.shopme.shipping.ShippingRateNotFoundException;
import com.shopme.shipping.ShippingRateService;
import com.shopme.shoppingcart.ShoppingCartService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller class for handling checkout operations.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/checkout")
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final CustomerService customerService;
    private final AddressService addressService;
    private final ShippingRateService shippingRateService;
    private final ShoppingCartService shoppingCartService;
    private final OrderService orderService;

    /**
     * Handles GET requests to the /checkout endpoint.
     * Prepares the checkout page with necessary attributes.
     *
     * @param model   The model to add attributes to.
     * @param request The HTTP request.
     * @return The name of the view to render.
     */
    @GetMapping
    public String showCheckoutPage(Model model, HttpServletRequest request) {
        var customer = getAuthenticatedCustomer(request);

        Address defaultAddress = null;
        ShippingRate shippingRate = null;
        boolean usePrimaryAddressAsDefault = false;

        try {
            defaultAddress = addressService.getDefaultAddress(customer);
            model.addAttribute("shippingAddress", defaultAddress.toString());
            shippingRate = shippingRateService.getShippingRateForAddress(defaultAddress);
        } catch (AddressNotFoundException e) {
            model.addAttribute("shippingAddress", customer.toString());
            usePrimaryAddressAsDefault = true;
            try {
                shippingRate = shippingRateService.getShippingRateForCustomer(customer);
            } catch (ShippingRateNotFoundException ex) {
                return "redirect:/cart";
            }
        } catch (ShippingRateNotFoundException e) {
            return "redirect:/cart";
        }

        var cartItems = shoppingCartService.listCartItems(customer);
        var checkoutInfo = checkoutService.prepareCheckout(cartItems, shippingRate);

        model.addAttribute("checkoutInfo", checkoutInfo);
        model.addAttribute("cartItems", cartItems);

        return "checkout/checkout";
    }

    /**
     * Retrieves the authenticated customer from the HTTP request.
     *
     * @param request The HTTP request.
     * @return The authenticated customer.
     */
    private Customer getAuthenticatedCustomer(HttpServletRequest request) {
        var email = Utility.getEmailOfAuthenticatedCustomer(request);
        return customerService.getCustomerByEmail(email);
    }

    /**
     * Handles POST requests to the /place_order endpoint.
     * Places an order for the authenticated customer.
     *
     * @param request The HTTP request.
     * @return The name of the view to render.
     */
    @PostMapping("/place_order")
    public String placeOrder(HttpServletRequest request) {
        String paymentType = request.getParameter("paymentMethod");
        PaymentMethod paymentMethod = PaymentMethod.valueOf(paymentType);

        var customer = getAuthenticatedCustomer(request);

        Address defaultAddress = null;
        ShippingRate shippingRate = null;
        boolean usePrimaryAddressAsDefault = false;

        try {
            defaultAddress = addressService.getDefaultAddress(customer);
            shippingRate = shippingRateService.getShippingRateForAddress(defaultAddress);
        } catch (AddressNotFoundException e) {
            usePrimaryAddressAsDefault = true;
        } catch (ShippingRateNotFoundException e) {
        }

        var cartItems = shoppingCartService.listCartItems(customer);
        var checkoutInfo = checkoutService.prepareCheckout(cartItems, shippingRate);

        orderService.createOrder(customer, defaultAddress, cartItems, paymentMethod, checkoutInfo);
        shoppingCartService.deleteByCustomer(customer);

        return "checkout/order_completed";
    }

}