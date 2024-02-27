package com.shopme.admin.product;

import com.shopme.common.entity.Product;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    public static final int PRODUCTS_PER_PAGE = 5;
    private final ProductRepository productRepository;

    public List<Product> listAll() {
        return (List<Product>) productRepository.findAll();
    }

    public Page<Product> listByPage(int pageNum, String sortField, String sortDir, String keyword, Integer categoryId) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE, sort);

        if (keyword != null && !keyword.isEmpty()) {
            if (categoryId != null && categoryId > 0) {
                return productRepository.searchInCategory(categoryId, keyword, pageable);
            }
            return productRepository.findAll(keyword, pageable);
        }

        if (categoryId != null && categoryId > 0) {
            return productRepository.findAllInCategory(categoryId, pageable);
        }

        return productRepository.findAll(pageable);
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

    public Product getById(Integer id) throws ProductNotFoundException {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Could not find any product with ID " + id));
    }
}
