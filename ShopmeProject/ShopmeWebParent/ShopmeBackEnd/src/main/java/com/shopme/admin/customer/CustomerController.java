package com.shopme.admin.customer;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping()
    public String getFirstPage(Model model) {
        return listByPage(model, 1, "firstName", "asc", null);
    }

    @GetMapping("/page/{pageNum}")
    public String listByPage(Model model,
                             @PathVariable int pageNum,
                             @RequestParam String sortField,
                             @RequestParam String sortDir,
                             @RequestParam(required = false) String keyword) {

        Page<Customer> page = customerService.listByPage(pageNum, sortField, sortDir, keyword);
        List<Customer> listCustomers = page.getContent();

        long startCount = (pageNum - 1) * CustomerService.CUSTOMERS_PER_PAGE + 1;
        model.addAttribute("startCount", startCount);

        long endCount = startCount + CustomerService.CUSTOMERS_PER_PAGE - 1;
        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }

        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("listCustomers", listCustomers);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("reversedSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("endCount", endCount);
        model.addAttribute("moduleURL", "/customers");

        return "customers/customers";
    }

    @GetMapping("/detail/{id}")
    public String viewCustomerDetails(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        try {
            Customer customer = customerService.getById(id);
            model.addAttribute("customer", customer);

            return "customer_detail_modal3";
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
