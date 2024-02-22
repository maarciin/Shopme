package com.shopme.admin.product;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.brand.BrandService;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Product;
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
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final BrandService brandService;

    @GetMapping
    public String listAll(Model model) {
        List<Product> listProducts = productService.listAll();
        model.addAttribute("listProducts", listProducts);
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

        return "products/product_form";
    }

    @PostMapping("/save")
    public String saveProduct(Product productToSave, RedirectAttributes redirectAttributes,
                              @RequestParam(name = "fileImage") MultipartFile mainImage,
                              @RequestParam(name = "extraImage") MultipartFile[] extraImages,
                              @RequestParam(name = "detailNames", required = false) String[] detailNames,
                              @RequestParam(name = "detailValues", required = false) String[] detailValues) throws IOException {
        setMainImageName(mainImage, productToSave);
        setExtraImageNames(extraImages, productToSave);
        setProductDetails(detailNames, detailValues, productToSave);

        Product savedProduct = productService.save(productToSave);
        saveUploadedImages(mainImage, extraImages, savedProduct);

        redirectAttributes.addFlashAttribute("message", "The product has been saved successfully.");
        return "redirect:/products";
    }

    private void saveUploadedImages(MultipartFile mainImage, MultipartFile[] extraImages, Product product) throws IOException {
        if (!mainImage.isEmpty()) {
            String fileName = StringUtils.cleanPath(mainImage.getOriginalFilename());
            String uploadDir = "../product-images/" + product.getId();

            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, mainImage);
        }

        if (extraImages.length > 0) {
            String uploadDir = "../product-images/" + product.getId() + "/extras";

            for (MultipartFile file : extraImages) {
                if (file.isEmpty()) continue;

                String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                FileUploadUtil.saveFile(uploadDir, fileName, file);
            }
        }
    }

    private void setMainImageName(MultipartFile fileImage, Product product) {
        if (!fileImage.isEmpty()) {
            String fileName = StringUtils.cleanPath(fileImage.getOriginalFilename());
            product.setMainImage(fileName);
        }
    }

    private void setExtraImageNames(MultipartFile[] extraImages, Product product) {
        if (extraImages.length > 0) {
            for (MultipartFile file : extraImages) {
                if (!file.isEmpty()) {
                    String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                    product.addExtraImage(fileName);
                }
            }
        }
    }

    private void setProductDetails(String[] detailNames, String[] detailValues, Product productToSave) {
        if (detailNames == null || detailNames.length == 0) return;

        for (int i = 0; i < detailNames.length; i++) {
            String name = detailNames[i];
            String value = detailValues[i];
            if (!name.isEmpty() && !value.isEmpty()) {
                productToSave.addDetail(name, value);
            }
        }
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

}
