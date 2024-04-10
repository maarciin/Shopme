package com.shopme.checkout.paypal;

import com.shopme.setting.PaymentSettingBag;
import com.shopme.setting.SettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * This class provides services related to PayPal transactions.
 * It is responsible for validating and retrieving order details from PayPal.
 */
@Component
@RequiredArgsConstructor
public class PayPalService {

    private static final String GET_ORDER_API = "/v2/checkout/orders/";
    private final SettingService settingService;
    private final RestTemplate restTemplate;

    /**
     * Validates the order by retrieving its details from PayPal and checking if the order ID matches.
     *
     * @param orderId The ID of the order to be validated.
     * @return true if the order is valid, false otherwise.
     * @throws PayPalApiException if there is an error while retrieving the order details.
     */
    public boolean validateOrder(String orderId) throws PayPalApiException {
        PayPalOrderResponse orderResponse = getOrderDetails(orderId);
        return orderResponse.validate(orderId);
    }

    /**
     * Retrieves the details of an order from PayPal.
     *
     * @param orderId The ID of the order to be retrieved.
     * @return The details of the order.
     * @throws PayPalApiException if there is an error while retrieving the order details.
     */
    private PayPalOrderResponse getOrderDetails(String orderId) throws PayPalApiException {
        ResponseEntity<PayPalOrderResponse> response = makeRequest(orderId);

        HttpStatus statusCode = (HttpStatus) response.getStatusCode();

        if (!statusCode.equals(HttpStatus.OK)) {
            throwExceptionForNonOKResponse(statusCode);
        }

        return response.getBody();
    }

    /**
     * Makes a request to PayPal to retrieve the details of an order.
     *
     * @param orderId The ID of the order to be retrieved.
     * @return The response from PayPal.
     */
    private ResponseEntity<PayPalOrderResponse> makeRequest(String orderId) {
        PaymentSettingBag paymentSettings = settingService.getPaymentSettings();
        String baseURL = paymentSettings.getPaypalApiURL();
        String requestURL = baseURL + GET_ORDER_API + orderId;
        String clientId = paymentSettings.getPaypalClientId();
        String clientSecret = paymentSettings.getPaypalClientSecret();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.add("Accept-Language", "en_US");
        headers.setBasicAuth(clientId, clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

        return restTemplate.exchange(requestURL, HttpMethod.GET, request, PayPalOrderResponse.class);
    }

    /**
     * Throws a PayPalApiException with a message based on the status code.
     *
     * @param statusCode The status code of the response from PayPal.
     * @throws PayPalApiException with a message based on the status code.
     */
    private static void throwExceptionForNonOKResponse(HttpStatus statusCode) throws PayPalApiException {
        String message = null;
        switch (statusCode) {
            case NOT_FOUND -> message = "Order ID not found";
            case BAD_REQUEST -> message = "Bad Request to PayPal Checkout API";
            case INTERNAL_SERVER_ERROR -> message = "PayPal Internal Server Error";
            default -> message = "PayPal returned non-OK status code: " + statusCode;
        }
        throw new PayPalApiException(message);
    }

}