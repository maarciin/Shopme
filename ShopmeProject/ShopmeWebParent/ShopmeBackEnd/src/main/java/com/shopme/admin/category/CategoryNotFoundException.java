package com.shopme.admin.category;

public class CategoryNotFoundException extends Exception {
    CategoryNotFoundException(String message) {
        super(message);
    }
}
