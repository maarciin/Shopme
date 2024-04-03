package com.shopme.admin.order;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.admin.setting.SettingService;
import com.shopme.common.entity.Setting;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private String defaultRedirectUrl = "redirect:/orders/page/1?sortField=orderTime&sortDir=desc";
    private final OrderService orderService;
    private final SettingService settingService;

    @GetMapping
    public String listFirstPage() {
        return defaultRedirectUrl;
    }

    @GetMapping("/page/{pageNum}")
    public String listByPage(@PagingAndSortingParam(listName = "listOrders", moduleURL = "/orders") PagingAndSortingHelper helper,
                             @PathVariable int pageNum, HttpServletRequest request) {
        orderService.listByPage(pageNum, helper);
        loadCurrencySettings(request);
        return "orders/orders";
    }

    private void loadCurrencySettings(HttpServletRequest request) {
        List<Setting> currencySettings = settingService.getCurrencySettings();

        currencySettings.forEach(setting -> request.setAttribute(setting.getKey(), setting.getValue()));
    }
}
