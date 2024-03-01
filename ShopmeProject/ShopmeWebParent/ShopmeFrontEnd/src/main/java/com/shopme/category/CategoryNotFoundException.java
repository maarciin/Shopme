package com.shopme.category;

public class CategoryNotFoundException extends Exception {
    CategoryNotFoundException(String message) {
        super(message);
    }
}
