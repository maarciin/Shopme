package com.shopme.admin.order;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.common.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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

    public Order getOrder(Integer id) throws OrderNotFoundException {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Could not find any order with ID " + id));
    }

    public void delete(Integer id) throws OrderNotFoundException {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Could not find any order with ID " + id));
        orderRepository.delete(order);
    }
}