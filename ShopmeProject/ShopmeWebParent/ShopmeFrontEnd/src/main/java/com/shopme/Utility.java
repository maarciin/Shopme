package com.shopme;

import com.shopme.security.oauth.CustomerOAuth2User;
import com.shopme.setting.CurrencySettingBag;
import com.shopme.setting.EmailSettingBag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.security.Principal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Properties;

public class Utility {

    public static String getSiteUrl(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }

    public static JavaMailSenderImpl prepareMailSender(EmailSettingBag settings) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(settings.getHost());
        mailSender.setPort(settings.getPort());
        mailSender.setUsername(settings.getUsername());
        mailSender.setPassword(settings.getPassword());

        Properties mailProperties = new Properties();
        mailProperties.setProperty("mail.smtp.auth", settings.getSmtpAuth());
        mailProperties.setProperty("mail.smtp.starttls.enable", settings.getSmtpSecured());

        mailSender.setJavaMailProperties(mailProperties);

        return mailSender;
    }

    public static String getEmailOfAuthenticatedCustomer(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal == null) {
            return null;
        }

        String customerEmail = null;

        if (principal instanceof UsernamePasswordAuthenticationToken || principal instanceof RememberMeAuthenticationToken) {
            customerEmail = principal.getName();
        } else if (principal instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) principal;
            CustomerOAuth2User oAuth2User = (CustomerOAuth2User) oauth2Token.getPrincipal();
            customerEmail = oAuth2User.getEmail();
        }
        return customerEmail;
    }

    /**
     * This method formats the currency amount based on the settings provided in the CurrencySettingBag object.
     *
     * @param amount   The amount to be formatted.
     * @param settings The CurrencySettingBag object containing the currency settings.
     * @return The formatted currency amount.
     */
    public static String formatCurrency(float amount, CurrencySettingBag settings) {
        String symbol = settings.getSymbol();
        String symbolPosition = settings.getSymbolPosition();
        String decimalPointType = settings.getDecimalPointType();
        String thousandPointType = settings.getThousandPointType();
        int decimalDigits = settings.getDecimalDigits();

        String numberPattern = "###,###." + "#".repeat(decimalDigits);
        String patternWithSymbol = symbolPosition.equals("Before Price") ? symbol + numberPattern : numberPattern + symbol;

        DecimalFormatSymbols formatSymbols = DecimalFormatSymbols.getInstance();
        formatSymbols.setDecimalSeparator(decimalPointType.charAt(0));
        formatSymbols.setGroupingSeparator(thousandPointType.charAt(0));

        DecimalFormat formatter = new DecimalFormat(patternWithSymbol, formatSymbols);

        return formatter.format(amount);
    }

}
