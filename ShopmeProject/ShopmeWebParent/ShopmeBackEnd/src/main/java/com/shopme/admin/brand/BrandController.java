package com.shopme.admin.brand;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.category.CategoryService;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
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

    @PostMapping("/save")
    public String createNewBrand(Brand brandToSave, @RequestParam MultipartFile fileImage,
                                 RedirectAttributes redirectAttributes) throws IOException {

        if (!fileImage.isEmpty()) {
            String fileName = StringUtils.cleanPath(fileImage.getOriginalFilename());
            brandToSave.setLogo(fileName);

            Brand savedBrand = brandService.save(brandToSave);
            String uploadDir = "../brand-logos/" + savedBrand.getId();

            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, fileImage);
        } else {
            brandService.save(brandToSave);
        }
        redirectAttributes.addFlashAttribute("message", "The brand has been saved successfully.");
        return "redirect:/brands";
    }

    @GetMapping("/edit/{id}")
    public String editBrand(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Brand editedBrand = brandService.getById(id);
            List<Category> listCategories = categoryService.listCategoriesUsedInForm();

            model.addAttribute("brand", editedBrand);
            model.addAttribute("listCategories", listCategories);
            model.addAttribute("pageTitle", "Edit Brand (ID: " + id + ")");
            return "brands/brand_form";

        } catch (BrandNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/brands";
        }
    }

}
