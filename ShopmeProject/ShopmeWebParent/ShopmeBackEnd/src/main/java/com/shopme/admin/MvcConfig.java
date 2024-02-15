package com.shopme.admin;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String dirName = "user-photos";
        Path userPhotosDir = Paths.get(dirName);

        String userPhotosPath = userPhotosDir.toFile().getAbsolutePath();

        registry.addResourceHandler("/" + dirName + "/**")
                .addResourceLocations("file:" + userPhotosPath + "/");

        String categoryImagesName = "../category-images";
        Path categoryImagesDir = Paths.get(categoryImagesName);

        String categoryImagesPath = categoryImagesDir.toFile().getAbsolutePath();

        registry.addResourceHandler("/category-images/**")
                .addResourceLocations("file:" + categoryImagesPath + "/");

        String brandLogosName = "../brand-logos";
        Path brandLogosDir = Paths.get(brandLogosName);

        String brandLogosPath = brandLogosDir.toFile().getAbsolutePath();

        registry.addResourceHandler("/brand-logos/**")
                .addResourceLocations("file:" + brandLogosPath + "/");
    }
}
