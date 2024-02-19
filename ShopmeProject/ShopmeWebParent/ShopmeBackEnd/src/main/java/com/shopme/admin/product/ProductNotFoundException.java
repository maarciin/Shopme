package com.shopme.admin.product;

public class ProductNotFoundException extends Exception {
    ProductNotFoundException(String message) {
        super(message);
    }
}
