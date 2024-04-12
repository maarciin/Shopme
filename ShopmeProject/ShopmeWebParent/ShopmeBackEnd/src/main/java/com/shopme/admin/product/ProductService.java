package com.shopme.admin.product;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.common.entity.product.Product;
import com.shopme.common.exception.ProductNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public void listByPage(int pageNum, PagingAndSortingHelper helper, Integer categoryId) {

        Pageable pageable = helper.createPageable(PRODUCTS_PER_PAGE, pageNum);
        String keyword = helper.getKeyword();
        Page<Product> page = null;

        if (keyword != null && !keyword.isEmpty()) {
            if (categoryId != null && categoryId > 0) {
                page = productRepository.searchInCategory(categoryId, keyword, pageable);
            } else {
                page = productRepository.findAll(keyword, pageable);
            }
        }

        if (categoryId != null && categoryId > 0) {
            page = productRepository.findAllInCategory(categoryId, pageable);
        } else {
            page = productRepository.findAll(pageable);
        }

        helper.updateModelAttributes(pageNum, page);
    }

    public void searchProducts(int pageNum, PagingAndSortingHelper helper) {
        Pageable pageable = helper.createPageable(PRODUCTS_PER_PAGE, pageNum);
        String keyword = helper.getKeyword();

        Page<Product> page = productRepository.searchProductsByName(keyword, pageable);
        helper.updateModelAttributes(pageNum, page);
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

    public void saveProductPrice(Product productInForm) {
        productRepository.findById(productInForm.getId())
                .ifPresentOrElse(p -> {
                            p.setCost(productInForm.getCost());
                            p.setPrice(productInForm.getPrice());
                            p.setDiscountPercent(productInForm.getDiscountPercent());
                            productRepository.save(p);
                        },
                        () -> new ProductNotFoundException("Could not find any product with ID " + productInForm.getId())
                );
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
