package com.shopme.admin.brand;

import com.shopme.admin.category.CategoryService;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/brands")
public class BrandController {

    private final BrandService brandService;
    private final CategoryService categoryService;

    @GetMapping
    public String listAll(Model model) {
        List<Brand> brands = brandService.findAll();
        model.addAttribute("listBrands", brands);
        return "brands/brands";
    }

    @GetMapping("/new")
    public String newBrand(Model model) {
        List<Category> listCategories = categoryService.listCategoriesUsedInForm();

        model.addAttribute("listCategories", listCategories);
        model.addAttribute("brand", new Brand());
        model.addAttribute("pageTitle", "Create New Brand");

        return "brands/brand_form";
    }

}
