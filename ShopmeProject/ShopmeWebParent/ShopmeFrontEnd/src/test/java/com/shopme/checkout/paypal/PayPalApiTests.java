package com.shopme.checkout.paypal;

import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class PayPalApiTests {

    private static final String BASE_URL = "https://api.sandbox.paypal.com";
    private static final String GET_ORDER_API = "/v2/checkout/orders/";
    private static final String CLIENT_ID = "PAYPAL_CLIENT_ID";

    private static final String CLIENT_SECRET = "PAYPAL_CLIENT_SECRET";

    @Test
    public void testGetOrderDetails_whenOrderIdDoesNotExist_shouldReturn404() {
        String orderId = "1TC97737PU401321A";
        String requestURL = BASE_URL + GET_ORDER_API + orderId;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.add("Accept-Language", "en_US");
        headers.setBasicAuth(CLIENT_ID, CLIENT_SECRET);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<PayPalOrderResponse> response = restTemplate.exchange(requestURL, HttpMethod.GET, request, PayPalOrderResponse.class);
        PayPalOrderResponse orderResponse = response.getBody();

        System.out.println("Order ID: " + orderResponse.getId());
        System.out.println("Validated: " + orderResponse.validate(orderId));
    }

}
