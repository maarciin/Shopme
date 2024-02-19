package com.shopme.admin.product;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductRestController {

    private final ProductService productService;

    @PostMapping("/products/check_unique")
    public String checkDuplicateBrand(@RequestParam(required = false) Integer id, @RequestParam String name) {
        return productService.isProductUnique(id, name) ? "OK" : "Duplicated";
    }

}
