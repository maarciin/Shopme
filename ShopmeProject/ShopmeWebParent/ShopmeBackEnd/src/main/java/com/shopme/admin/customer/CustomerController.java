package com.shopme.admin.customer;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping()
    public String getFirstPage() {
        return "redirect:/customers/page/1?sortField=firstName&sortDir=asc";
    }

    @GetMapping("/page/{pageNum}")
    public String listByPage(@PagingAndSortingParam(listName = "listCustomers", moduleURL = "/customers") PagingAndSortingHelper helper,
                             @PathVariable int pageNum) {
        customerService.listByPage(pageNum, helper);
        return "customers/customers";
    }

    @GetMapping("/detail/{id}")
    public String viewCustomerDetails(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        try {
            Customer customer = customerService.getById(id);
            model.addAttribute("customer", customer);

            return "customers/customer_detail_modal";
        } catch (CustomerNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
            return "redirect:/customers";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            customerService.deleteCustomer(id);

            String message = "Customer with ID " + id + " has been deleted successfully.";
            ra.addFlashAttribute("message", message);
        } catch (CustomerNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
        }

        return "redirect:/customers";
    }

    @GetMapping("{id}/enabled/{status}")
    public String enableCustomer(@PathVariable Integer id, @PathVariable boolean status, RedirectAttributes ra) {
        try {
            customerService.updateCustomerEnabledStatus(id, status);

            String enabledDisabled = status ? "enabled" : "disabled";
            String message = "The customer ID " + id + " has been " + enabledDisabled;
            ra.addFlashAttribute("message", message);
        } catch (CustomerNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
        }

        return "redirect:/customers";
    }

    @GetMapping("/edit/{id}")
    public String editCustomer(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        try {
            Customer customer = customerService.getById(id);
            List<Country> countries = customerService.listAllCountries();

            model.addAttribute("customer", customer);
            model.addAttribute("listCountries", countries);
            model.addAttribute("pageTitle", "Edit Customer (ID: " + id + ")");
            return "customers/customer_form";

        } catch (CustomerNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
            return "redirect:/customers";
        }
    }

    @PostMapping("/save")
    public String saveCustomer(Customer customer, RedirectAttributes ra) {
        customerService.save(customer);
        ra.addFlashAttribute("message", "The customer ID " + customer.getId() + " has been updated successfully.");
        return "redirect:/customers";
    }
}
