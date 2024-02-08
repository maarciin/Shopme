package com.shopme.admin.category;

import com.shopme.common.entity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/categories")
    public String getCategories(Model model) {
        List<Category> listCategories = categoryService.listAll();
        model.addAttribute("listCategories", listCategories);
        return "categories/categories";
    }

}
