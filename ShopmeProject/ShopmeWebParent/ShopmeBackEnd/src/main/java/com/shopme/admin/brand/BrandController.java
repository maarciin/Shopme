package com.shopme.admin.brand;

import com.shopme.common.entity.Brand;
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

    @GetMapping
    public String listAll(Model model) {
        List<Brand> brands = brandService.findAll();
        model.addAttribute("listBrands", brands);
        return "brands/brands";
    }

}
