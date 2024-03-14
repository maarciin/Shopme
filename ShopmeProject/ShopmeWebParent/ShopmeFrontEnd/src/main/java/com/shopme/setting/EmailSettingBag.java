package com.shopme.setting;

import com.shopme.common.entity.Setting;
import com.shopme.common.entity.SettingBag;

import java.util.List;

public class EmailSettingBag extends SettingBag {
    public EmailSettingBag(List<Setting> listSettings) {
        super(listSettings);
    }

    public String getHost() {
        return super.getValue("MAIL_HOST").orElseThrow();
    }

    public Integer getPort() {
        return Integer.parseInt(super.getValue("MAIL_PORT").orElseThrow());
    }

    public String getUsername() {
        return super.getValue("MAIL_USERNAME").orElseThrow();
    }

    public String getPassword() {
        return super.getValue("MAIL_PASSWORD").orElseThrow();
    }

    public String getSmtpAuth() {
        return super.getValue("SMTP_AUTH").orElseThrow();
    }

    public String getSmtpSecured() {
        return super.getValue("SMTP_SECURED").orElseThrow();
    }

    public String getFromAddress() {
        return super.getValue("MAIL_FROM").orElseThrow();
    }

    public String getSenderName() {
        return super.getValue("MAIL_SENDER_NAME").orElseThrow();
    }

    public String getCustomerVerifySubject() {
        return super.getValue("CUSTOMER_VERIFY_SUBJECT").orElseThrow();
    }

    public String getCustomerVerifyContent() {
        return super.getValue("CUSTOMER_VERIFY_CONTENT").orElseThrow();
    }
}
