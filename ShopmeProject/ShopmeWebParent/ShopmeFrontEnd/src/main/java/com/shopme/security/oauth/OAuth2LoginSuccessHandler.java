package com.shopme.security.oauth;

import com.shopme.common.entity.AuthenticationType;
import com.shopme.common.entity.Customer;
import com.shopme.customer.CustomerService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final CustomerService customerService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {

        CustomerOAuth2User oAuth2User = (CustomerOAuth2User) authentication.getPrincipal();
        String name = oAuth2User.getName();
        String email = oAuth2User.getEmail();
        String countryCode = request.getLocale().getCountry();
        String clientName = oAuth2User.getClientName();

        Customer customer = customerService.getCustomerByEmail(email);
        AuthenticationType authenticationType = getAuthenticationType(clientName);

        if (customer == null) {
            customerService.addNewCustomerUponOAuthLogin(name, email, countryCode, authenticationType);
        } else {
            customerService.updateAuthenticationType(customer, authenticationType);
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }

    private AuthenticationType getAuthenticationType(String clientName) {
        if (clientName.equals("Facebook")) {
            return AuthenticationType.FACEBOOK;
        } else if (clientName.equals("Google")) {
            return AuthenticationType.GOOGLE;
        } else {
            return AuthenticationType.DATABASE;
        }
    }
}
