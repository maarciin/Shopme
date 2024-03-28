package com.shopme.address;

import com.shopme.Utility;
import com.shopme.common.entity.Address;
import com.shopme.common.entity.Customer;
import com.shopme.customer.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/address_book")
public class AddressController {

    private final AddressService addressService;
    private final CustomerService customerService;

    @GetMapping
    public String showAddressBook(Model model, HttpServletRequest request) {
        var customer = getAuthenticatedCustomer(request);
        var listAddresses = addressService.listAddressBook(customer);

        boolean usePrimaryAddressAsDefault = listAddresses.stream().noneMatch(Address::isDefaultForShipping);

        model.addAttribute("listAddresses", listAddresses);
        model.addAttribute("customer", customer);
        model.addAttribute("usePrimaryAddressAsDefault", usePrimaryAddressAsDefault);
        return "address_book/addresses";
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request) {
        var email = Utility.getEmailOfAuthenticatedCustomer(request);
        return customerService.getCustomerByEmail(email);
    }
}
