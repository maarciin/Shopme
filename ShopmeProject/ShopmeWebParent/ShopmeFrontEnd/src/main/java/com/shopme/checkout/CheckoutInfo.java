package com.shopme.checkout;

import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@Getter
@Setter
public class CheckoutInfo {
    private float productCost;
    private float productTotal;
    private float shippingCostTotal;
    private float paymentTotal;
    private int deliveryDays;
    private boolean codSupported;

    public Date getDeliveryDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, deliveryDays);

        return calendar.getTime();
    }


    public String getPaymentTotal4PayPal() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat formatter = new DecimalFormat("0.00", symbols);
        return formatter.format(paymentTotal);
    }
}
