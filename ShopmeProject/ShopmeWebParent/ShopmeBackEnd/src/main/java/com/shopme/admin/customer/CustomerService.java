package com.shopme.admin.customer;

import com.shopme.common.entity.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public List<Customer> findAll() {
        return customerRepository.findAll();
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
}
