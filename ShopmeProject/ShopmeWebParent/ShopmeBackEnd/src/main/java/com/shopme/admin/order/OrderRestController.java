package com.shopme.admin.order;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderRestController {

    private final OrderService orderService;

    @PostMapping("/orders_shipper/update/{id}/{status}")
    public Response updateOrderStatus(@PathVariable("id") Integer orderId, @PathVariable String status) {
        orderService.updateStatus(orderId, status);
        return new Response(orderId, status);
    }

}

record Response(Integer orderId, String status) {
}
