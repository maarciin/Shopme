package com.shopme.admin.shippingrate;

public class ShippingRateAlreadyExistsException extends Exception{
    ShippingRateAlreadyExistsException(String message) {
        super(message);
    }
}
