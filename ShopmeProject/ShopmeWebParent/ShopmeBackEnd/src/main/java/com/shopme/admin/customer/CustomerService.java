package com.shopme.admin.customer;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.setting.country.CountryRepository;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    public static final int CUSTOMERS_PER_PAGE = 10;

    private final CustomerRepository customerRepository;
    private final CountryRepository countryRepository;
    private final PasswordEncoder passwordEncoder;

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    public void listByPage(int pageNum, PagingAndSortingHelper helper) {
        helper.listEntities(pageNum, CUSTOMERS_PER_PAGE, customerRepository);
    }

    public Customer getById(Integer id) throws CustomerNotFoundException {
        return customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Could not find any customer with ID " + id));
    }

    public void deleteCustomer(Integer id) throws CustomerNotFoundException {
        Long countById = customerRepository.countById(id);
        if (countById == null || countById == 0) {
            throw new CustomerNotFoundException("Could not find any customer with ID " + id);
        }
        customerRepository.deleteById(id);
    }

    @Transactional
    public void updateCustomerEnabledStatus(Integer id, boolean status) throws CustomerNotFoundException {
        Long countById = customerRepository.countById(id);
        if (countById == null || countById == 0) {
            throw new CustomerNotFoundException("Could not find any customer with ID " + id);
        }
        customerRepository.updateEnabledStatus(id, status);
    }

    public List<Country> listAllCountries() {
        return countryRepository.findAllByOrderByName();
    }

    public boolean isEmailUnique(Integer id, String email) {
        Optional<Customer> customer = customerRepository.findByEmail(email);

        return customer.isEmpty() || Objects.equals(customer.get().getId(), id);
    }

    public void save(Customer customerInForm) {
        Customer customerInDB = customerRepository.findById(customerInForm.getId()).get();

        if (!customerInForm.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(customerInForm.getPassword());
            customerInForm.setPassword(encodedPassword);
        } else {
            customerInForm.setPassword(customerInDB.getPassword());
        }

        customerInForm.setEnabled(customerInDB.isEnabled());
        customerInForm.setCreatedTime(customerInDB.getCreatedTime());
        customerInForm.setVerificationCode(customerInDB.getVerificationCode());
        customerInForm.setAuthenticationType(customerInDB.getAuthenticationType());
        customerInForm.setResetPasswordToken(customerInDB.getResetPasswordToken());

        customerRepository.save(customerInForm);
    }
}
