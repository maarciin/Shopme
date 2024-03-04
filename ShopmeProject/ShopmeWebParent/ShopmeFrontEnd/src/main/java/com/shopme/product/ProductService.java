package com.shopme.product;

import com.shopme.common.entity.Product;
import com.shopme.common.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    public static final int PRODUCTS_PER_PAGE = 10;
    public static final int SEARCH_RESULTS_PER_PAGE = 10;

    private final ProductRepository productRepository;

    public Page<Product> listByCategory(int pageNum, Integer categoryId) {
        Pageable pageable = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE);
        return productRepository.listByCategory(categoryId, pageable);
    }

    public Product getByAlias(String alias) throws ProductNotFoundException {
        return productRepository.findByAlias(alias)
                .orElseThrow(() -> new ProductNotFoundException("Could not find any product with alias " + alias));
    }

    public Page<Product> search(String keyword, int pageNum) {
        Pageable pageable = PageRequest.of(pageNum - 1, SEARCH_RESULTS_PER_PAGE);
        return productRepository.search(keyword, pageable);
    }

}
