package com.shopme.admin.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;

    @PostMapping("/users/check_email")
    public String checkDuplicateEmail(@RequestParam(required = false) Integer id, @RequestParam String email) {
        return userService.isEmailUnique(id, email) ? "OK" :"Duplicated";
    }

}
