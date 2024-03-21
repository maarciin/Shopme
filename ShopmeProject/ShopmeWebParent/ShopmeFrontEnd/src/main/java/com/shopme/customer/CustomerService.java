package com.shopme.customer;

import com.shopme.common.entity.AuthenticationType;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.setting.CountryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import net.bytebuddy.utility.RandomString;
/**
 * Service class for handling customer-related operations.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CountryRepository countryRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Fetches all countries from the repository and sorts them by name.
     * @return List of all countries sorted by name.
     */
    public List<Country> listAllCountries() {
        return countryRepository.findAllByOrderByName();
    }

    /**
     * Checks if the provided email is unique.
     * @param email Email to check.
     * @return true if the email is unique, false otherwise.
     */
    public boolean isEmailUnique(String email) {
        return customerRepository.findByEmail(email).isEmpty();
    }

    /**
     * Registers a new customer.
     * @param customer Customer to register.
     */
    public void registerCustomer(Customer customer) {
        encodePassword(customer);
        customer.setEnabled(false);
        customer.setCreatedTime(LocalDateTime.now());
        customer.setAuthenticationType(AuthenticationType.DATABASE);

        String randomCode = RandomString.make(64);
        customer.setVerificationCode(randomCode);

        customerRepository.save(customer);
    }

    /**
     * Fetches a customer by their email.
     * @param email Email of the customer.
     * @return Customer object if found, null otherwise.
     */
    public Customer getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email).orElse(null);
    }

    /**
     * Encodes the password of a customer.
     * @param customer Customer whose password is to be encoded.
     */
    private void encodePassword(Customer customer) {
        String encodedPassword = passwordEncoder.encode(customer.getPassword());
        customer.setPassword(encodedPassword);
    }

    /**
     * Verifies a customer using a verification code.
     * @param verificationCode Verification code to check.
     * @return true if the verification is successful, false otherwise.
     */
    public boolean verify(String verificationCode) {
        Optional<Customer> customer = customerRepository.findByVerificationCode(verificationCode);

        if (customer.isEmpty() || customer.get().isEnabled()) {
            return false;
        } else {
            customerRepository.enableCustomer(customer.get().getId());
            return true;
        }
    }

    /**
     * Updates the authentication type of customer.
     * @param customer Customer whose authentication type is to be updated.
     * @param type New authentication type.
     */
    public void updateAuthenticationType(Customer customer, AuthenticationType type) {
        if (!customer.getAuthenticationType().equals(type)) {
            customerRepository.updateAuthenticationType(customer.getId(), type);
        }
    }

    /**
     * Adds a new customer upon OAuth login.
     * @param name Name of the customer.
     * @param email Email of the customer.
     * @param countryCode Country code of the customer.
     */
    public void addNewCustomerUponOAuthLogin(String name, String email, String countryCode, AuthenticationType authenticationType) {
        Customer customer = new Customer();
        customer.setEmail(email);
        setName(name, customer);
        customer.setEnabled(true);
        customer.setCreatedTime(LocalDateTime.now());
        customer.setAuthenticationType(authenticationType);
        customer.setPassword("");
        customer.setAddressLine1("");
        customer.setCity("");
        customer.setState("");
        customer.setPhoneNumber("");
        customer.setPostalCode("");
        Country defaultCountryPoland = countryRepository.findById(180).get();
        countryRepository.findByCode(countryCode)
                .ifPresentOrElse(customer::setCountry, () -> customer.setCountry(defaultCountryPoland));

        customerRepository.save(customer);
    }

    /**
     * Sets the name of a customer.
     * @param name Full name of the customer.
     * @param customer Customer whose name is to be set.
     */
    private void setName(String name, Customer customer) {
        String[] nameParts = name.split(" ");
        if (nameParts.length < 2) {
            customer.setFirstName(name);
            customer.setLastName("");
        } else {
            customer.setFirstName(nameParts[0]);
            String lastName = name.replaceFirst(nameParts[0], "").trim();
            customer.setLastName(lastName);
        }
    }

}