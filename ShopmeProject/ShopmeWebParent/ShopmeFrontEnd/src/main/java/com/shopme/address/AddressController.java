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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller class for managing customer addresses.
 * It handles HTTP requests related to the address book.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/address_book")
public class AddressController {

    private final AddressService addressService;
    private final CustomerService customerService;

    /**
     * Handles the GET request to show the address book.
     *
     * @param model   The model to add attributes to.
     * @param request The HTTP request.
     * @return The name of the view to render.
     */
    @GetMapping
    public String showAddressBook(Model model, HttpServletRequest request) {
        var customer = getAuthenticatedCustomer(request);
        var listAddresses = addressService.listAddressBook(customer);

        var usePrimaryAddressAsDefault = listAddresses.stream().noneMatch(Address::isDefaultForShipping);

        model.addAttribute("listAddresses", listAddresses);
        model.addAttribute("customer", customer);
        model.addAttribute("usePrimaryAddressAsDefault", usePrimaryAddressAsDefault);
        return "address_book/addresses";
    }

    /**
     * Handles the GET request to show the address form.
     *
     * @param model The model to add attributes to.
     * @return The name of the view to render.
     */
    @GetMapping("/new")
    public String showAddressForm(Model model) {
        var address = new Address();
        var listCountries = customerService.listAllCountries();

        model.addAttribute("address", address);
        model.addAttribute("listCountries", listCountries);
        model.addAttribute("pageTitle", "Add New Address");

        return "address_book/address_form";
    }

    /**
     * Handles the POST request to save an address.
     *
     * @param address The address to save.
     * @param request The HTTP request.
     * @param ra      The redirect attributes to add flash attributes to.
     * @return The URL to redirect to.
     */
    @PostMapping("/save")
    public String saveAddress(Address address, HttpServletRequest request, RedirectAttributes ra) {
        var customer = getAuthenticatedCustomer(request);
        address.setCustomer(customer);
        addressService.save(address);
        ra.addFlashAttribute("message", "The address has been saved successfully.");
        return "redirect:/address_book";
    }

    /**
     * Handles the GET request to edit an address.
     *
     * @param id      The ID of the address to edit.
     * @param model   The model to add attributes to.
     * @param request The HTTP request.
     * @param ra      The redirect attributes to add flash attributes to.
     * @return The name of the view to render, or the URL to redirect to if an error occurs.
     */
    @GetMapping("/edit/{id}")
    public String editAddress(@PathVariable Integer id, Model model, HttpServletRequest request, RedirectAttributes ra) {
        var customer = getAuthenticatedCustomer(request);

        try {
            var address = addressService.get(id, customer.getId());
            var listCountries = customerService.listAllCountries();
            model.addAttribute("address", address);
            model.addAttribute("listCountries", listCountries);
            model.addAttribute("pageTitle", "Edit Address (ID: " + id + ")");

            return "address_book/address_form";
        } catch (AddressNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
            return "redirect:/address_book";
        }

    }

    /**
     * Handles the GET request to delete an address.
     *
     * @param id      The ID of the address to delete.
     * @param request The HTTP request.
     * @param ra      The redirect attributes to add flash attributes to.
     * @return The URL to redirect to.
     */
    @GetMapping("/delete/{id}")
    public String deleteAddress(@PathVariable Integer id, HttpServletRequest request, RedirectAttributes ra) {
        var customer = getAuthenticatedCustomer(request);
        addressService.delete(id, customer.getId());
        ra.addFlashAttribute("message", "The address has been deleted successfully.");
        return "redirect:/address_book";
    }

    /**
     * Handles the GET request to set an address as the default address.
     *
     * @param id      The ID of the address to set as default.
     * @param request The HTTP request to get the authenticated customer from.
     * @return The URL to redirect to.
     */

    @GetMapping("/default/{id}")
    public String setDefaultAddress(@PathVariable Integer id, HttpServletRequest request) {
        var customer = getAuthenticatedCustomer(request);
        addressService.setDefaultAddress(id, customer.getId());
        return "redirect:/address_book";
    }

    /**
     * Retrieves the authenticated customer from the request.
     *
     * @param request The HTTP request.
     * @return The authenticated customer.
     */
    private Customer getAuthenticatedCustomer(HttpServletRequest request) {
        var email = Utility.getEmailOfAuthenticatedCustomer(request);
        return customerService.getCustomerByEmail(email);
    }

}