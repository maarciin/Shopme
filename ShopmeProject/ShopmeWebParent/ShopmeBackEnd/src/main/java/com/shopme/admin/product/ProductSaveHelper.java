package com.shopme.admin.product;

import com.shopme.admin.FileUploadUtil;
import com.shopme.common.entity.Product;
import com.shopme.common.entity.ProductImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class ProductSaveHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    static void deleteExtraImagesRemovedOnForm(Product product) {
        String extraImageDir = "../product-images/" + product.getId() + "/extras";
        Path dirpath = Paths.get(extraImageDir);

        try {
            Files.list(dirpath).forEach(file -> {
                String filename = file.toFile().getName();

                if (!product.containsImageName(filename)) {
                    try {
                        Files.delete(file);
                        LOGGER.error("Deleted extra image: " + filename);
                    } catch (IOException e) {
                        LOGGER.error("Could not delete extra image: " + filename);
                    }
                }
            });
        } catch (IOException e) {
            LOGGER.error("Could not list directory: " + dirpath);
        }

    }

    static void setExistingExtraImageNames(String[] imageIds, String[] imageNames, Product product) {
        if (imageIds == null || imageIds.length == 0) return;

        Set<ProductImage> images = new HashSet<>();
        for (int i = 0; i < imageIds.length; i++) {
            Integer id = Integer.parseInt(imageIds[i]);
            String name = imageNames[i];
            images.add(new ProductImage(id, name, product));
        }

        product.setImages(images);
    }

    static void saveUploadedImages(MultipartFile mainImage, MultipartFile[] extraImages, Product product) throws IOException {
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

    static void setMainImageName(MultipartFile fileImage, Product product) {
        if (!fileImage.isEmpty()) {
            String fileName = StringUtils.cleanPath(fileImage.getOriginalFilename());
            product.setMainImage(fileName);
        }
    }

    static void setNewExtraImageNames(MultipartFile[] extraImages, Product product) {
        if (extraImages.length > 0) {
            for (MultipartFile file : extraImages) {
                if (!file.isEmpty()) {
                    String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                    if (!product.containsImageName(fileName)) {
                        product.addExtraImage(fileName);
                    }
                }
            }
        }
    }

    static void setProductDetails(String[] detailsIds, String[] detailNames, String[] detailValues, Product productToSave) {
        if (detailNames == null || detailNames.length == 0) return;

        for (int i = 0; i < detailNames.length; i++) {
            String name = detailNames[i];
            String value = detailValues[i];
            int id = Integer.parseInt(detailsIds[i]);
            if (id != 0) {
                productToSave.addDetail(id, name, value);
            } else if (!name.isEmpty() && !value.isEmpty()) {
                productToSave.addDetail(name, value);
            }
        }
    }
}
