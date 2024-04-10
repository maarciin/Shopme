package com.shopme.common.entity.order;

import com.shopme.common.entity.AbstractAddress;
import com.shopme.common.entity.Address;
import com.shopme.common.entity.Customer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order extends AbstractAddress {

    @Column(length = 45, nullable = false)
    private String country;

    private Date orderTime;

    private float shippingCost;

    private float productCost;

    private float subtotal;

    private float tax;

    private float total;

    private int deliveryDays;

    private Date deliveryDate;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private Set<OrderDetail> orderDetails = new HashSet<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderTrack> orderTracks = new ArrayList<>();

    public void copyAddressFromCustomer() {
        setFirstName(customer.getFirstName());
        setLastName(customer.getLastName());
        setPhoneNumber(customer.getPhoneNumber());
        setAddressLine1(customer.getAddressLine1());
        setAddressLine2(customer.getAddressLine2());
        setCity(customer.getCity());
        setState(customer.getState());
        setCountry(customer.getCountry().getName());
        setPostalCode(customer.getPostalCode());
    }

    @Override
    public String toString() {
        return "Order [id=" + id + ", subtotal=" + subtotal + ", paymentMethod=" + paymentMethod + ", status=" + status
                + ", customer=" + customer.getFullName() + "]";
    }

    @Transient
    public String getDestination() {
        String destination = city + ", ";
        if (state != null && !state.isEmpty()) destination += state + ", ";
        destination += country;

        return destination;
    }

    public void copyShippingAddress(Address address) {
        setFirstName(address.getFirstName());
        setLastName(address.getLastName());
        setPhoneNumber(address.getPhoneNumber());
        setAddressLine1(address.getAddressLine1());
        setAddressLine2(address.getAddressLine2());
        setCity(address.getCity());
        setState(address.getState());
        setCountry(address.getCountry().getName());
        setPostalCode(address.getPostalCode());
    }

    @Transient
    public String getShippingAddress() {
        String address = firstName;

        if (lastName != null && !lastName.isEmpty()) {
            address += " " + lastName;
        }

        if (addressLine1 != null && !addressLine1.isEmpty()) {
            address += ", " + addressLine1;
        }

        if (addressLine2 != null && !addressLine2.isEmpty()) {
            address += ", " + addressLine2;
        }

        if (city != null && !city.isEmpty()) {
            address += ", " + city;
        }

        if (state != null && !state.isEmpty()) {
            address += ", " + state;
        }

        address += ", " + country;

        if (postalCode != null && !postalCode.isEmpty()) {
            address += ". Postal Code: " + postalCode;
        }

        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            address += ". Phone Number: " + phoneNumber;
        }

        return address;

    }
}
