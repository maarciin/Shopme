package com.shopme.checkout;

import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.ShippingRate;
import com.shopme.common.entity.product.Product;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for handling checkout operations.
 */
@Service
public class CheckoutService {

    private static final int DIM_DIVISOR = 139;

    /**
     * Prepares the checkout information for a given list of cart items and a shipping rate.
     *
     * @param cartItems    The list of cart items.
     * @param shippingRate The shipping rate.
     * @return The checkout information.
     */
    public CheckoutInfo prepareCheckout(List<CartItem> cartItems, ShippingRate shippingRate) {
        CheckoutInfo checkoutInfo = new CheckoutInfo();

        float productCost = calculateProductCost(cartItems);
        float productTotal = calculateProductTotal(cartItems);
        float shippingCostTotal = calculateShippingCost(cartItems, shippingRate);
        float paymentTotal = productTotal + shippingCostTotal;

        checkoutInfo.setProductCost(productCost);
        checkoutInfo.setProductTotal(productTotal);
        checkoutInfo.setDeliveryDays(shippingRate.getDays());
        checkoutInfo.setCodSupported(shippingRate.isCodSupported());
        checkoutInfo.setShippingCostTotal(shippingCostTotal);
        checkoutInfo.setPaymentTotal(paymentTotal);

        return checkoutInfo;
    }

    /**
     * Calculates the total product cost for a given list of cart items.
     *
     * @param cartItems The list of cart items.
     * @return The total product cost.
     */
    private float calculateProductCost(List<CartItem> cartItems) {
        return cartItems.stream()
                .map(item -> item.getQuantity() * item.getProduct().getCost())
                .reduce(Float::sum)
                .orElse(0.0F);
    }

    /**
     * Calculates the total product cost for a given list of cart items.
     *
     * @param cartItems The list of cart items.
     * @return The total product cost.
     */
    private float calculateProductTotal(List<CartItem> cartItems) {
        return cartItems.stream()
                .map(CartItem::getSubtotal)
                .reduce(Float::sum)
                .orElse(0.0F);
    }

    /**
     * Calculates the total shipping cost for a given list of cart items and a shipping rate.
     *
     * @param cartItems    The list of cart items.
     * @param shippingRate The shipping rate.
     * @return The total shipping cost.
     */
    private float calculateShippingCost(List<CartItem> cartItems, ShippingRate shippingRate) {
        float shippingCostTotal = 0.0F;

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            float dimWeight = (product.getLength() * product.getWidth() * product.getHeight()) / DIM_DIVISOR;
            float finalWeight = Math.max(dimWeight, product.getWeight());
            float shippingCost = finalWeight * cartItem.getQuantity() * shippingRate.getRate();

            cartItem.setShippingCost(shippingCost);

            shippingCostTotal += shippingCost;
        }

        return shippingCostTotal;
    }
}