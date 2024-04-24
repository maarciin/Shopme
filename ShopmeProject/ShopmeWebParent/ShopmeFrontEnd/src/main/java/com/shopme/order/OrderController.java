package com.shopme.order;

import com.shopme.Utility;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.order.Order;
import com.shopme.customer.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final CustomerService customerService;

    @GetMapping("/orders")
    public String listFirstPage(Model model, HttpServletRequest request) {
        return listOrdersByPage(model, request, 1, "orderTime", "desc", null);
    }

    @GetMapping("/orders/page/{pageNum}")
    public String listOrdersByPage(Model model, HttpServletRequest request, @PathVariable int pageNum,
                                   String sortField, String sortDir, String orderKeyword) {

        Customer customer = getAuthenticatedCustomer(request);
        Page<Order> page = orderService.listForCustomerByPage(customer, pageNum, sortField, sortDir, orderKeyword);
        List<Order> listOrders = page.getContent();

        long startCount = (long) (pageNum - 1) * OrderService.ORDERS_PER_PAGE + 1;
        long endCount = startCount + OrderService.ORDERS_PER_PAGE - 1;

        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }

        model.addAttribute("listOrders", listOrders);
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reversedSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("moduleURL", "/orders");
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("keyword", orderKeyword);

        return "order/orders_customer";
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request) {
        var email = Utility.getEmailOfAuthenticatedCustomer(request);
        return customerService.getCustomerByEmail(email);
    }
}
