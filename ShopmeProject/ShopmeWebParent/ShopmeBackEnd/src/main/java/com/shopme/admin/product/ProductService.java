package com.shopme.admin.product;

import com.shopme.common.entity.Product;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> listAll() {
        return (List<Product>) productRepository.findAll();
    }

    public Product save(Product product) {
        if (product.getId() == null) {
            product.setCreatedTime(new Date());
        }

        if (product.getAlias() == null || product.getAlias().isEmpty()) {
            String defaultAlias = product.getName().replaceAll(" ", "-");
            product.setAlias(defaultAlias);
        } else {
            product.setAlias(product.getName().replaceAll(" ", "-"));
        }

        product.setUpdatedTime(new Date());
        return productRepository.save(product);
    }

    public boolean isProductUnique(Integer id, String name) {
        Optional<Product> product = productRepository.findByName(name);
        if (product.isEmpty()) return true;

        if (id == null) {
            return false;
        } else {
            return Objects.equals(product.get().getId(), id);
        }
    }

    public void updateProductEnabledStatus(Integer id, boolean status) {
        productRepository.updateEnabledStatus(id, status);
    }

    public void deleteProduct(Integer id) throws ProductNotFoundException {
        Long countById = productRepository.countById(id);
        if (countById == null || countById == 0) {
            throw new ProductNotFoundException("Could not find any product with ID " + id);
        }
        productRepository.deleteById(id);
    }
}
