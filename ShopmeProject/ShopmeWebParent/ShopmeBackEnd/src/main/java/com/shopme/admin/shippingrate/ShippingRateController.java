package com.shopme.admin.shippingrate;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.ShippingRate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller class for managing shipping rates.
 * It handles HTTP requests related to shipping rates.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/shipping_rates")
public class ShippingRateController {

    private final ShippingRateService shippingRateService;
    private final String defaultRedirectURL = "redirect:/shipping_rates/page/1?sortField=country&sortDir=asc";

    /**
     * Handles the GET request to list the first page of shipping rates.
     *
     * @return The URL to redirect to.
     */
    @GetMapping
    public String listFirstPage() {
        return defaultRedirectURL;
    }

    /**
     * Handles the GET request to list shipping rates by page number.
     *
     * @param pageNum The page number to retrieve.
     * @param helper  The helper object that handles paging and sorting.
     * @return The name of the view to render.
     */
    @GetMapping("/page/{pageNum}")
    public String listShippingRates(@PathVariable int pageNum,
                                    @PagingAndSortingParam(moduleURL = "/shipping_rates",
                                            listName = "shippingRates") PagingAndSortingHelper helper) {

        shippingRateService.listByPage(pageNum, helper);
        return "shipping_rates/shipping_rates";
    }

    /**
     * Handles the GET request to create a new shipping rate.
     *
     * @param model The model to add attributes to.
     * @return The name of the view to render.
     */
    @GetMapping("/new")
    public String createShippingRate(Model model) {
        List<Country> listCountries = shippingRateService.listAllCountries();

        model.addAttribute("rate", new ShippingRate());
        model.addAttribute("listCountries", listCountries);
        model.addAttribute("pageTitle", "New Shipping Rate");

        return "shipping_rates/shipping_rate_form";
    }

    /**
     * Handles the POST request to save a shipping rate.
     *
     * @param rateToSave The shipping rate to save.
     * @param ra         The redirect attributes to add flash attributes to.
     * @return The URL to redirect to.
     */
    @PostMapping("/save")
    public String saveShippingRate(ShippingRate rateToSave, RedirectAttributes ra) {
        try {
            shippingRateService.save(rateToSave);
            ra.addFlashAttribute("message", "The shipping rate has been saved successfully.");
        } catch (ShippingRateAlreadyExistsException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
        }

        return defaultRedirectURL;
    }

    /**
     * Handles the GET request to edit a shipping rate.
     *
     * @param id    The ID of the shipping rate to edit.
     * @param model The model to add attributes to.
     * @param ra    The redirect attributes to add flash attributes to.
     * @return The name of the view to render, or the URL to redirect to if an error occurs.
     */
    @GetMapping("/edit/{id}")
    public String editShippingRate(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        try {
            ShippingRate rate = shippingRateService.get(id);
            List<Country> listCountries = shippingRateService.listAllCountries();

            model.addAttribute("rate", rate);
            model.addAttribute("listCountries", listCountries);
            model.addAttribute("pageTitle", "Edit Shipping Rate (ID: " + id + ")");

            return "shipping_rates/shipping_rate_form";
        } catch (ShippingRateNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
            return defaultRedirectURL;
        }
    }

    /**
     * Handles the GET request to enable or disable COD support for a shipping rate.
     *
     * @param id        The ID of the shipping rate to update.
     * @param supported The new COD support status.
     * @param ra        The redirect attributes to add flash attributes to.
     * @return The URL to redirect to.
     */
    @GetMapping("/cod/{id}/enabled/{supported}")
    public String enableCDOSupport(@PathVariable Integer id, @PathVariable boolean supported, RedirectAttributes ra) {
        try {
            shippingRateService.updateCODSupportStatus(id, supported);
            String updated = supported ? "enabled" : "disabled";
            String message = String.format("COD support for Shipping Rate ID %d has been %s", id, updated);
            ra.addFlashAttribute("message", message);
        } catch (ShippingRateNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
        }

        return defaultRedirectURL;
    }

    /**
     * Handles the GET request to delete a shipping rate.
     *
     * @param id The ID of the shipping rate to delete.
     * @param ra The redirect attributes to add flash attributes to.
     * @return The URL to redirect to.
     */
    @GetMapping("/delete/{id}")
    public String deleteShippingRate(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            shippingRateService.deleteShippingRate(id);
            ra.addFlashAttribute("message", "The shipping rate ID " + id + " has been deleted successfully");
        } catch (ShippingRateNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
        }

        return defaultRedirectURL;
    }
}