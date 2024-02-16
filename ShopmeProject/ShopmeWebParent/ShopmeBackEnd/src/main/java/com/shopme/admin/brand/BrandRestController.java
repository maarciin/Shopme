package com.shopme.admin.brand;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BrandRestController {

    private final BrandService brandService;

    @PostMapping("/brands/check_email")
    public String checkDuplicateBrand(@RequestParam(required = false) Integer id, @RequestParam String name) {
        return brandService.isBrandUnique(id, name) ? "OK" : "Duplicated";
    }

}
