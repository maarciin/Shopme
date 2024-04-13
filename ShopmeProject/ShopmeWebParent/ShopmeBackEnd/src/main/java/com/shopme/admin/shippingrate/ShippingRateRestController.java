package com.shopme.admin.shippingrate;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * This is a Rest Controller for handling shipping rate related requests.
 * It uses the ShippingRateService to perform the business logic related to shipping rates.
 */
@RestController
@RequiredArgsConstructor
public class ShippingRateRestController {

    private final ShippingRateService shippingRateService;

    /**
     * This method is a POST endpoint that calculates the shipping cost for a given product, country and state.
     * It uses the ShippingRateService to calculate the shipping cost.
     *
     * @param productId The ID of the product for which the shipping cost is to be calculated.
     * @param countryId The ID of the country to which the product is to be shipped.
     * @param state     The state to which the product is to be shipped.
     * @return The shipping cost as a string.
     * @throws ShippingRateNotFoundException If no shipping rate is found for the given parameters.
     */
    @PostMapping("/get_shipping_cost")
    public String getShippingCost(@RequestParam Integer productId, @RequestParam Integer countryId, @RequestParam String state) throws ShippingRateNotFoundException {
        return String.valueOf(shippingRateService.calculateShippingCost(productId, countryId, state));
    }
}