package com.shopme.setting;

import com.shopme.common.entity.setting.Setting;
import com.shopme.common.entity.setting.SettingBag;

import java.util.List;

public class PaymentSettingBag extends SettingBag {
    public PaymentSettingBag(List<Setting> listSettings) {
        super(listSettings);
    }

    public String getPaypalApiURL() {
        return super.getValue("PAYPAL_API_BASE_URL").orElseThrow();
    }

    public String getPaypalClientId() {
        return super.getValue("PAYPAL_API_CLIENT_ID").orElseThrow();
    }

    public String getPaypalClientSecret() {
        return super.getValue("PAYPAL_API_CLIENT_SECRET").orElseThrow();
    }

}
