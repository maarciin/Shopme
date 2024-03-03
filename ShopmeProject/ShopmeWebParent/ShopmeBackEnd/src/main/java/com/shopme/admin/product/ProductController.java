package com.shopme.admin.product;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.brand.BrandService;
import com.shopme.admin.category.CategoryService;
import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;
import com.shopme.common.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    public final static String ASCENDING_ORDER = "asc";
    public final static String DESCENDING_ORDER = "desc";

    private final ProductService productService;
    private final BrandService brandService;
    private final CategoryService categoryService;

    @GetMapping
    public String listAll(Model model) {
        return listByPage(1, model, "name", ASCENDING_ORDER, null, 0);
    }

    @GetMapping("/page/{pageNum}")
    public String listByPage(@PathVariable int pageNum, Model model, @RequestParam String sortField,
                             @RequestParam String sortDir, @RequestParam(required = false) String keyword,
                             @RequestParam(required = false) Integer categoryId) {
        System.out.println("selected category id: " + categoryId);
        Page<Product> page = productService.listByPage(pageNum, sortField, sortDir, keyword, categoryId);
        List<Product> listProducts = page.getContent();

        long startCount = (pageNum - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
        long endCount = startCount + ProductService.PRODUCTS_PER_PAGE - 1;

        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }

        String reversedSortDir = sortDir.equals(ASCENDING_ORDER) ? DESCENDING_ORDER : ASCENDING_ORDER;

        List<Category> listCategories = categoryService.listCategoriesUsedInForm();

        if (categoryId != null) {
            model.addAttribute("categoryId", categoryId);
        }

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("listProducts", listProducts);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reversedSortDir", reversedSortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("listCategories", listCategories);

        return "products/products";
    }

    @GetMapping("/new")
    public String newProduct(Model model) {
        List<Brand> listBrands = brandService.listAll();
        Product product = new Product();
        product.setEnabled(true);
        product.setInStock(true);

        model.addAttribute("listBrands", listBrands);
        model.addAttribute("product", product);
        model.addAttribute("pageTitle", "Create New Product");
        model.addAttribute("numberOfExistingExtraImages", 0);

        return "products/product_form";
    }

    @PostMapping("/save")
    public String saveProduct(Product productToSave, RedirectAttributes redirectAttributes,
                              @RequestParam(name = "fileImage", required = false) MultipartFile mainImage,
                              @RequestParam(name = "extraImage", required = false) MultipartFile[] extraImages,
                              @RequestParam(required = false) String[] detailNames,
                              @RequestParam(required = false) String[] detailIds,
                              @RequestParam(required = false) String[] detailValues,
                              @RequestParam(required = false) String[] imageIds,
                              @RequestParam(required = false) String[] imageNames,
                              @AuthenticationPrincipal ShopmeUserDetails loggedUser) throws IOException {
        if (loggedUser.hasRole("Salesperson")) {
            productService.saveProductPrice(productToSave);
            redirectAttributes.addFlashAttribute("message", "The product has been saved successfully.");
            return "redirect:/products";
        }

        ProductSaveHelper.setMainImageName(mainImage, productToSave);
        ProductSaveHelper.setExistingExtraImageNames(imageIds, imageNames, productToSave);
        ProductSaveHelper.setNewExtraImageNames(extraImages, productToSave);
        ProductSaveHelper.setProductDetails(detailIds, detailNames, detailValues, productToSave);

        Product savedProduct = productService.save(productToSave);
        ProductSaveHelper.saveUploadedImages(mainImage, extraImages, savedProduct);

        ProductSaveHelper.deleteExtraImagesRemovedOnForm(productToSave);

        redirectAttributes.addFlashAttribute("message", "The product has been saved successfully.");
        return "redirect:/products";
    }



    @GetMapping("/{id}/enabled/{status}")
    public String updateProductEnabledStatus(@PathVariable Integer id, @PathVariable boolean status,
                                             RedirectAttributes redirectAttributes) {
        productService.updateProductEnabledStatus(id, status);
        String enabledDisabled = status ? "enabled" : "disabled";
        String message = "The product ID " + id + " has been " + enabledDisabled;
        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/products";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            String productExtraImagesDir = "../product-images/" + id + "/extras";
            String productImagesDir = "../product-images/" + id;
            FileUploadUtil.removeDir(productExtraImagesDir);
            FileUploadUtil.removeDir(productImagesDir);

            String message = "Product with ID " + id + " has been deleted successfully.";
            redirectAttributes.addFlashAttribute("message", message);
        } catch (ProductNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }

        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String editProduct(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.getById(id);
            List<Brand> listBrands = brandService.listAll();
            Integer numberOfExistingExtraImages = product.getImages().size();

            model.addAttribute("product", product);
            model.addAttribute("listBrands", listBrands);
            model.addAttribute("pageTitle", "Edit Product (ID: " + id + ")");
            model.addAttribute("numberOfExistingExtraImages", numberOfExistingExtraImages);

            return "products/product_form";
        } catch (ProductNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/products";
        }
    }

    @GetMapping("/detail/{id}")
    public String viewProductDetails(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.getById(id);
            model.addAttribute("product", product);

            return "products/product_detail_modal";
        } catch (ProductNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/products";
        }
    }

}
