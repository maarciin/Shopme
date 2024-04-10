package com.shopme.checkout;

import com.shopme.Utility;
import com.shopme.address.AddressNotFoundException;
import com.shopme.address.AddressService;
import com.shopme.common.entity.Address;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.ShippingRate;
import com.shopme.common.entity.order.Order;
import com.shopme.common.entity.order.PaymentMethod;
import com.shopme.customer.CustomerService;
import com.shopme.order.OrderService;
import com.shopme.setting.CurrencySettingBag;
import com.shopme.setting.EmailSettingBag;
import com.shopme.setting.SettingService;
import com.shopme.shipping.ShippingRateNotFoundException;
import com.shopme.shipping.ShippingRateService;
import com.shopme.shoppingcart.ShoppingCartService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
    private final SettingService settingService;

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

        var currencyCode = settingService.getCurrencyCode();
        var paymentSettings = settingService.getPaymentSettings();
        var paypalClientId = paymentSettings.getPaypalClientId();

        model.addAttribute("customer", customer);
        model.addAttribute("checkoutInfo", checkoutInfo);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("currencyCode", currencyCode);
        model.addAttribute("paypalClientId", paypalClientId);

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
    public String placeOrder(HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
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

        Order createdOrder = orderService.createOrder(customer, defaultAddress, cartItems, paymentMethod, checkoutInfo);
        shoppingCartService.deleteByCustomer(customer);

        sendConfirmationEmail(createdOrder);

        return "checkout/order_completed";
    }

    /**
     * Sends a confirmation email to the customer for the given order.
     *
     * @param order The order to send the email for.
     * @throws MessagingException           If an error occurs while sending the email.
     * @throws UnsupportedEncodingException If an error occurs while encoding the email content.
     */
    private void sendConfirmationEmail(Order order) throws MessagingException, UnsupportedEncodingException {
        EmailSettingBag emailSettings = settingService.getEmailSettings();
        JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);
        mailSender.setDefaultEncoding("utf-8");

        String toAddress = order.getCustomer().getEmail();
        String subject = emailSettings.getOrderConfirmationSubject();
        subject = subject.replace("[[orderId]]", String.valueOf(order.getId()));

        String content = emailSettings.getOrderConfirmationContent();

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
        helper.setTo(toAddress);
        helper.setSubject(subject);

        DateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss E, dd MMM yyyy");
        String orderTime = dateFormatter.format(order.getOrderTime());

        CurrencySettingBag currencySettings = settingService.getCurrencySettings();
        String totalAmount = Utility.formatCurrency(order.getTotal(), currencySettings);

        content = content.replace("[[name]]", order.getCustomer().getFullName());
        content = content.replace("[[orderId]]", String.valueOf(order.getId()));
        content = content.replace("[[orderTime]]", orderTime);
        content = content.replace("[[shippingAddress]]", order.getShippingAddress());
        content = content.replace("[[total]]", totalAmount);
        content = content.replace("[[paymentMethod]]", order.getPaymentMethod().toString());

        helper.setText(content, true);
        mailSender.send(message);
    }

}