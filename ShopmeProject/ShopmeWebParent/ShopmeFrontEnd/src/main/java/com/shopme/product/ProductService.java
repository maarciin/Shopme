package com.shopme.product;

import com.shopme.common.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    public static final int PRODUCTS_PER_PAGE = 10;

    private final ProductRepository productRepository;

    public Page<Product> listByCategory(int pageNum, Integer categoryId) {
        Pageable pageable = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE);
        return productRepository.listByCategory(categoryId, pageable);
    }

}
