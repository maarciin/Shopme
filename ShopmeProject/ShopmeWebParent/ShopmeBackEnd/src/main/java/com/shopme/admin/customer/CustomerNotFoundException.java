package com.shopme.admin.customer;

public class CustomerNotFoundException extends Exception {
    CustomerNotFoundException(String message) {
        super(message);
    }
}
