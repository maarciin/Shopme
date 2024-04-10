package com.shopme.checkout.paypal;

public class PayPalApiException extends Exception {
    PayPalApiException(String message) {
        super(message);
    }
}
