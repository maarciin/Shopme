package com.shopme.admin.category.controller;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.category.CategoryPageInfo;
import com.shopme.admin.category.CategoryService;
import com.shopme.admin.category.export.CategoryCsvExporter;
import com.shopme.common.entity.Category;
import com.shopme.common.exception.CategoryNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/categories")
    public String listFirstPage(@RequestParam(required = false) String sortDir, Model model) {
        return listByPage(1, sortDir, null, model);
    }

    @GetMapping("/categories/page/{pageNum}")
    public String listByPage(@PathVariable int pageNum, @RequestParam String sortDir,
                             @RequestParam(required = false) String keyword, Model model) {
        if (sortDir == null || sortDir.isEmpty()) {
            sortDir = "asc";
        }

        CategoryPageInfo pageInfo = new CategoryPageInfo();

        List<Category> listCategories = categoryService.listByPage(pageInfo, pageNum, sortDir, keyword);
        model.addAttribute("listCategories", listCategories);

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
        model.addAttribute("reverseSortDir", reverseSortDir);

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalItems", pageInfo.getTotalElements());
        model.addAttribute("totalPages", pageInfo.getTotalPages());

        long startCount = (long) (pageNum - 1) * CategoryService.ROOT_CATEGORIES_PER_PAGE + 1;
        long endCount = startCount + CategoryService.ROOT_CATEGORIES_PER_PAGE - 1;

        if (endCount > pageInfo.getTotalElements()) {
            endCount = pageInfo.getTotalElements();
        }

        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("sortField", "name");
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("moduleURL", "/categories");

        return "categories/categories";
    }

    @GetMapping("/categories/new")
    public String newCategory(Model model) {
        List<Category> listCategories = categoryService.listCategoriesUsedInForm();

        model.addAttribute("category", new Category());
        model.addAttribute("listCategories", listCategories);
        model.addAttribute("pageTitle", "Create New Category");

        return "categories/category_form";
    }

    @PostMapping("/categories/save")
    public String saveCategory(Category category, @RequestParam MultipartFile fileImage,
                               RedirectAttributes ra) throws IOException {

        if (!fileImage.isEmpty()) {
            String fileName = StringUtils.cleanPath(fileImage.getOriginalFilename());
            category.setImage(fileName);
            Category savedCategory = categoryService.save(category);
            String uploadDir = "../category-images/" + savedCategory.getId();

            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, fileImage);
        } else {
            categoryService.save(category);
        }

        ra.addFlashAttribute("message", "The category has been saved successfully.");
        return "redirect:/categories";
    }

    @GetMapping("/categories/edit/{id}")
    public String editCategory(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Category category = categoryService.getById(id);
            List<Category> listCategories = categoryService.listCategoriesUsedInForm();

            model.addAttribute("category", category);
            model.addAttribute("listCategories", listCategories);
            model.addAttribute("pageTitle", "Edit Category (ID: " + id + ")");

            return "categories/category_form";
        } catch (CategoryNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/categories";
        }

    }

    @GetMapping("/categories/{id}/enabled/{status}")
    public String updateCategoryEnabledStatus(@PathVariable Integer id,
                                              @PathVariable boolean status, RedirectAttributes redirectAttributes) {

        categoryService.updateCategoryEnabledStatus(id, status);
        String enabledDisabled = status ? "enabled" : "disabled";
        String message = "The category ID " + id + " has been " + enabledDisabled;
        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/categories";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(id);
            String categoryDir = "../category-images/" + id;
            FileUploadUtil.removeDir(categoryDir);
            String message = "Category with ID " + id + " has been deleted successfully.";
            redirectAttributes.addFlashAttribute("message", message);
        } catch (CategoryNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/categories";
    }

    @GetMapping("/categories/export/csv")
    public void exportToCSV(HttpServletResponse response) throws IOException {
        List<Category> categories = categoryService.listCategoriesUsedInForm();
        CategoryCsvExporter csvExporter = new CategoryCsvExporter();
        csvExporter.export(categories, response);
    }

}
