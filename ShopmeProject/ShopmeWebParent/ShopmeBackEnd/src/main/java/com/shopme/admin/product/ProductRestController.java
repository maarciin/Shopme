package com.shopme.admin.product;

import com.shopme.common.entity.product.Product;
import com.shopme.common.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductRestController {

    private final ProductService productService;

    @PostMapping("/check_unique")
    public String checkDuplicateBrand(@RequestParam(required = false) Integer id, @RequestParam String name) {
        return productService.isProductUnique(id, name) ? "OK" : "Duplicated";
    }

    @GetMapping("/get/{id}")
    public ProductDTO getProductInfo(@PathVariable Integer id) throws ProductNotFoundException {
        Product product = productService.getById(id);
        return new ProductDTO(product.getName(), product.getMainImagePath(), product.getDiscountPrice(), product.getCost());
    }

}
