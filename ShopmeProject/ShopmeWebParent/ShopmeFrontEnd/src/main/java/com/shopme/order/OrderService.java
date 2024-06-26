package com.shopme.order;

import com.shopme.checkout.CheckoutInfo;
import com.shopme.common.entity.Address;
import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.order.*;
import com.shopme.common.entity.product.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Service class for handling order operations.
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    public static final int ORDERS_PER_PAGE = 5;
    private final OrderRepository orderRepository;

    /**
     * Creates a new order with the given parameters.
     *
     * @param customer      The customer who is placing the order.
     * @param address       The shipping address for the order.
     * @param cartItems     The items in the customer's cart.
     * @param paymentMethod The payment method chosen by the customer.
     * @param checkoutInfo  The checkout information for the order.
     * @return The created order.
     */
    public Order createOrder(Customer customer, Address address, List<CartItem> cartItems,
                             PaymentMethod paymentMethod, CheckoutInfo checkoutInfo) {
        Order newOrder = new Order();
        newOrder.setOrderTime(new Date());
        newOrder.setStatus(paymentMethod.equals(PaymentMethod.PAYPAL) ? OrderStatus.PAID : OrderStatus.NEW);
        newOrder.setCustomer(customer);
        newOrder.setProductCost(checkoutInfo.getProductCost());
        newOrder.setSubtotal(checkoutInfo.getProductTotal());
        newOrder.setShippingCost(checkoutInfo.getShippingCostTotal());
        newOrder.setTax(0.0f);
        newOrder.setTotal(checkoutInfo.getPaymentTotal());
        newOrder.setPaymentMethod(paymentMethod);
        newOrder.setDeliveryDays(checkoutInfo.getDeliveryDays());
        newOrder.setDeliveryDate(checkoutInfo.getDeliveryDate());

        // If no address is provided, use the customer's default address.
        if (address == null) {
            newOrder.copyAddressFromCustomer();
        } else {
            newOrder.copyShippingAddress(address);
        }

        Set<OrderDetail> orderDetails = newOrder.getOrderDetails();

        // For each item in the cart, create an order detail and add it to the order.
        cartItems.forEach(cartItem -> {
            Product product = cartItem.getProduct();

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(newOrder);
            orderDetail.setProduct(product);
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setUnitPrice(product.getDiscountPrice());
            orderDetail.setProductCost(product.getCost() * cartItem.getQuantity());
            orderDetail.setSubtotal(cartItem.getSubtotal());
            orderDetail.setShippingCost(cartItem.getShippingCost());

            orderDetails.add(orderDetail);
        });

        OrderTrack orderTrack = new OrderTrack();
        orderTrack.setOrder(newOrder);
        orderTrack.setStatus(OrderStatus.NEW);
        orderTrack.setNotes(OrderStatus.NEW.defaultDescription());
        orderTrack.setUpdatedTime(new Date());

        newOrder.getOrderTracks().add(orderTrack);

        // Save the new order to the database.
        return orderRepository.save(newOrder);
    }

    public Page<Order> listForCustomerByPage(Customer customer, int pageNum, String sortField, String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, ORDERS_PER_PAGE, sort);

        if (keyword != null) {
            return orderRepository.findAll(keyword, customer.getId(), pageable);
        }

        return orderRepository.findAll(customer.getId(), pageable);
    }

}