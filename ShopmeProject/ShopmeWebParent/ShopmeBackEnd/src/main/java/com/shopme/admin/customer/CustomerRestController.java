package com.shopme.admin.customer;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CustomerRestController {

    private final CustomerService customerService;

    @PostMapping("/customers/check_email")
    public String checkDuplicateEmail(Integer id, String email) {
        if (customerService.isEmailUnique(id, email)) {
            return "OK";
        } else {
            return "Duplicated";
        }
    }
}
