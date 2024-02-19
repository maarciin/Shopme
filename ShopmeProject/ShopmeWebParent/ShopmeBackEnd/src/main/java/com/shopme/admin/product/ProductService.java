package com.shopme.admin.product;

import com.shopme.common.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> listAll() {
        return (List<Product>) productRepository.findAll();
    }
}
