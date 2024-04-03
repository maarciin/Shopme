package com.shopme.admin.order;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.admin.setting.SettingService;
import com.shopme.common.entity.Order;
import com.shopme.common.entity.Setting;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller class for handling order related requests.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {
    private String defaultRedirectUrl = "redirect:/orders/page/1?sortField=orderTime&sortDir=desc";
    private final OrderService orderService;
    private final SettingService settingService;

    /**
     * Handles the request to list the first page of orders.
     *
     * @return the default redirect URL
     */
    @GetMapping
    public String listFirstPage() {
        return defaultRedirectUrl;
    }

    /**
     * Handles the request to list orders by page number.
     *
     * @param helper  the helper for paging and sorting
     * @param pageNum the page number
     * @param request the HTTP request
     * @return the view name for displaying the orders
     */
    @GetMapping("/page/{pageNum}")
    public String listByPage(@PagingAndSortingParam(listName = "listOrders", moduleURL = "/orders") PagingAndSortingHelper helper,
                             @PathVariable int pageNum, HttpServletRequest request) {
        orderService.listByPage(pageNum, helper);
        loadCurrencySettings(request);
        return "orders/orders";
    }

    /**
     * Handles the request to view the detail of an order.
     *
     * @param id      the ID of the order
     * @param model   the model for the view
     * @param ra      the redirect attributes
     * @param request the HTTP request
     * @return the view name for displaying the order detail
     */
    @GetMapping("/detail/{id}")
    public String viewOrderDetail(@PathVariable Integer id, Model model, RedirectAttributes ra, HttpServletRequest request) {
        try {
            Order order = orderService.getOrder(id);
            loadCurrencySettings(request);
            model.addAttribute("order", order);
            return "orders/order_detail_modal";
        } catch (OrderNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
            return defaultRedirectUrl;
        }
    }

    /**
     * Loads the currency settings and sets them as request attributes.
     *
     * @param request the HTTP request
     */
    private void loadCurrencySettings(HttpServletRequest request) {
        List<Setting> currencySettings = settingService.getCurrencySettings();

        currencySettings.forEach(setting -> request.setAttribute(setting.getKey(), setting.getValue()));
    }

    /**
     * Handles the request to delete an order.
     *
     * @param id the ID of the order
     * @param ra the redirect attributes
     * @return the default redirect URL
     */
    @GetMapping("/delete/{id}")
    public String deleteOrder(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            orderService.delete(id);
            ra.addFlashAttribute("message", "The order ID " + id + " has been deleted successfully");
        } catch (OrderNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
        }

        return defaultRedirectUrl;
    }
}