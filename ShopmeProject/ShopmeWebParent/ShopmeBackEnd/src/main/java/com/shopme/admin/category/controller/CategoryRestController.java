package com.shopme.admin.category.controller;

import com.shopme.admin.category.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CategoryRestController {

    private final CategoryService categoryService;

    @PostMapping("/categories/check_unique")
    public String checkUnique(@RequestParam(required = false) Integer id, @RequestParam String name, @RequestParam String alias) {
        return categoryService.checkUnique(id, name, alias);
    }
}
