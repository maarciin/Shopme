package com.shopme.customer;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CustomerRestController {

    private final CustomerService customerService;

    @PostMapping("/customers/check_unique_email")
    public String checkDuplicateEmail(@RequestParam String email) {
        return customerService.isEmailUnique(email) ? "OK" : "Duplicated";
    }
}
