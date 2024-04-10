package com.shopme.common.entity.order;

public enum OrderStatus {
    NEW("Order was placed by the customer"),
    CANCELLED("Order was rejected"),
    PROCESSING("Order is being processed"),
    PACKAGED("Products were packaged"),
    PICKED("Shipper picked the package"),
    SHIPPING("Shipper is delivering the package"),
    DELIVERED("Customer received products"),
    RETURN_REQUESTED("Customer sent request to return purchase"),
    RETURNED("Products were returned"),
    PAID("Customer has paid this order"),
    REFUNDED("Customer has been refunded");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String defaultDescription() {
        return description;
    }
}