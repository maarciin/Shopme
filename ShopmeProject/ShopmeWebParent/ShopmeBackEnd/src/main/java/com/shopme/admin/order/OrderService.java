package com.shopme.admin.order;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.setting.country.CountryRepository;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.order.Order;
import com.shopme.common.entity.order.OrderStatus;
import com.shopme.common.entity.order.OrderTrack;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Service class for managing orders.
 * It handles the business logic related to orders.
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private static final int ORDERS_PER_PAGE = 10;
    private final OrderRepository orderRepository;
    private final CountryRepository countryRepository;

    /**
     * Retrieves all orders.
     *
     * @return A list of all orders.
     */
    public List<Order> listAll() {
        return orderRepository.findAll();
    }

    /**
     * Lists the orders for the current page and updates the model attributes.
     *
     * @param pageNum The current page number.
     * @param helper  The helper object for handling pagination and sorting.
     */
    public void listByPage(int pageNum, PagingAndSortingHelper helper) {

        String sortField = helper.getSortField();
        String sortDir = helper.getSortDir();
        String keyword = helper.getKeyword();

        Sort sort = null;

        if ("destination".equals(sortField)) {
            sort = Sort.by("country").and(Sort.by("state")).and(Sort.by("city"));
        } else {
            sort = Sort.by(sortField);
        }

        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, ORDERS_PER_PAGE, sort);

        Page<Order> page = null;

        if (keyword != null) {
            page = orderRepository.findAll(keyword, pageable);
        } else {
            page = orderRepository.findAll(pageable);
        }

        helper.updateModelAttributes(pageNum, page);
    }

    /**
     * Retrieves an order by its ID.
     *
     * @param id The ID of the order.
     * @return The order with the given ID.
     * @throws OrderNotFoundException If no order is found with the given ID.
     */
    public Order getOrder(Integer id) throws OrderNotFoundException {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Could not find any order with ID " + id));
    }

    /**
     * Deletes an order by its ID.
     *
     * @param id The ID of the order to delete.
     * @throws OrderNotFoundException If no order is found with the given ID.
     */

    public void delete(Integer id) throws OrderNotFoundException {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Could not find any order with ID " + id));
        orderRepository.delete(order);
    }

    /**
     * Retrieves a list of all countries.
     *
     * @return A list of all countries.
     */
    public List<Country> listAllCountries() {
        return countryRepository.findAllByOrderByName();
    }

    /**
     * Saves an order.
     *
     * @param orderInForm The order to save.
     */
    public void save(Order orderInForm) throws OrderNotFoundException {
        Order orderInDB = orderRepository.findById(orderInForm.getId())
                .orElseThrow(() -> new OrderNotFoundException("Could not find any order with ID " + orderInForm.getId()));
        orderInForm.setOrderTime(orderInDB.getOrderTime());
        orderInForm.setCustomer(orderInDB.getCustomer());

        orderRepository.save(orderInForm);
    }

    /**
     * Updates the status of an order.
     * This method retrieves an order from the database using the provided order ID, and updates its status
     * to the provided status if the current status of the order is different from the provided status.
     * It also creates a new OrderTrack object, sets its status, updated time, and notes, and adds it to the
     * order's list of OrderTrack objects. Finally, it saves the updated order back to the database.
     *
     * @param orderId The ID of the order to update.
     * @param status  The new status to set on the order.
     */
    public void updateStatus(Integer orderId, String status) {
        Order orderInDB = orderRepository.findById(orderId).get();
        OrderStatus statusToUpdate = OrderStatus.valueOf(status);

        if (!orderInDB.hasStatus(statusToUpdate)) {
            List<OrderTrack> orderTracks = orderInDB.getOrderTracks();
            OrderTrack track = new OrderTrack();
            track.setOrder(orderInDB);
            track.setStatus(statusToUpdate);
            track.setUpdatedTime(new Date());
            track.setNotes(statusToUpdate.defaultDescription());

            orderTracks.add(track);

            orderInDB.setStatus(statusToUpdate);

            orderRepository.save(orderInDB);
        }
    }
}