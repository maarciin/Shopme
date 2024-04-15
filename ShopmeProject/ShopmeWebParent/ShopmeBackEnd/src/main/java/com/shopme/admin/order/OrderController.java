package com.shopme.admin.order;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.admin.setting.SettingService;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.order.Order;
import com.shopme.common.entity.order.OrderDetail;
import com.shopme.common.entity.order.OrderStatus;
import com.shopme.common.entity.order.OrderTrack;
import com.shopme.common.entity.product.Product;
import com.shopme.common.entity.setting.Setting;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;

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

    /**
     * Handles the request to edit an order.
     *
     * @param id    the ID of the order
     * @param model the model for the view
     * @param ra    the redirect attributes
     * @return the view name for editing the order
     */
    @GetMapping("/edit/{id}")
    public String editOrder(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        try {
            Order order = orderService.getOrder(id);

            List<Country> listCountries = orderService.listAllCountries();

            model.addAttribute("order", order);
            model.addAttribute("pageTitle", "Edit Order (ID: " + id + ")");
            model.addAttribute("listCountries", listCountries);

            return "orders/order_form";
        } catch (OrderNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
            return defaultRedirectUrl;
        }
    }

    /**
     * Handles the request to save an order.
     *
     * @param orderInForm the order to save
     * @param request     the HTTP request
     * @param ra          the redirect attributes
     * @return the default redirect URL
     */
    @PostMapping("/save")
    public String saveOrder(Order orderInForm, HttpServletRequest request, RedirectAttributes ra) {
        String countryName = request.getParameter("countryName");
        orderInForm.setCountry(countryName);

        updateProductDetails(orderInForm, request);
        updateOrderTracks(orderInForm, request);

        try {
            orderService.save(orderInForm);
            ra.addFlashAttribute("message", "The orderInForm ID " + orderInForm.getId() + " has been updated successfully");
        } catch (OrderNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
        }

        return defaultRedirectUrl;
    }


    private void updateProductDetails(Order order, HttpServletRequest request) {
        String[] detailIds = request.getParameterValues("detailId");
        String[] productIds = request.getParameterValues("productId");
        String[] productPrices = request.getParameterValues("productPrice");
        String[] productDetailCosts = request.getParameterValues("productDetailCost");
        String[] quantities = request.getParameterValues("quantity");
        String[] productSubtotals = request.getParameterValues("productSubtotal");
        String[] productShipCosts = request.getParameterValues("productShipCost");

        Set<OrderDetail> orderDetails = order.getOrderDetails();

        for (int i = 0; i < detailIds.length; i++) {
//            System.out.println("Detail ID: " + detailIds[i]);
//            System.out.println("\t Prodouct ID: " + productIds[i]);
//            System.out.println("\t Cost: " + productDetailCosts[i]);
//            System.out.println("\t Quantity: " + quantities[i]);
//            System.out.println("\t Subtotal: " + productSubtotals[i]);
//            System.out.println("\t Ship cost: " + productShipCosts[i]);

            OrderDetail orderDetail = new OrderDetail();
            Integer detailId = Integer.parseInt(detailIds[i]);
            if (detailId > 0) {
                orderDetail.setId(detailId);
            }

            orderDetail.setOrder(order);
            orderDetail.setProduct(new Product(Integer.parseInt(productIds[i])));
            orderDetail.setProductCost(Float.parseFloat(productDetailCosts[i]));
            orderDetail.setSubtotal(Float.parseFloat(productSubtotals[i]));
            orderDetail.setShippingCost(Float.parseFloat(productShipCosts[i]));
            orderDetail.setQuantity(Integer.parseInt(quantities[i]));
            orderDetail.setUnitPrice(Float.parseFloat(productPrices[i]));

            orderDetails.add(orderDetail);

        }
    }

    private void updateOrderTracks(Order order, HttpServletRequest request) {
        String[] trackIds = request.getParameterValues("trackId");
        String[] trackStatuses = request.getParameterValues("trackStatus");
        String[] trackDates = request.getParameterValues("trackDate");
        String[] trackNotes = request.getParameterValues("trackNotes");

        List<OrderTrack> orderTracks = order.getOrderTracks();
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");

        for (int i = 0; i < trackIds.length; i++) {
            OrderTrack trackRecord = new OrderTrack();

            int trackId = Integer.parseInt(trackIds[i]);
            if (trackId > 0) {
                trackRecord.setId(trackId);
            }

            trackRecord.setOrder(order);
            trackRecord.setStatus(OrderStatus.valueOf(trackStatuses[i]));
            trackRecord.setNotes(trackNotes[i]);

            try {
                trackRecord.setUpdatedTime(dateFormatter.parse(trackDates[i]));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            orderTracks.add(trackRecord);
        }
    }
}