package com.shopme.admin.brand;

import com.shopme.common.entity.Brand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;

    public List<Brand> findAll() {
        return (List<Brand>) brandRepository.findAll();
    }

    public Brand save(Brand brand) {
        return brandRepository.save(brand);
    }

    public Brand getById(Integer id) throws BrandNotFoundException {
        return brandRepository.findById(id)
                .orElseThrow(() -> new BrandNotFoundException("Could not find any category with ID " + id));
    }

    public void deleteBrand(Integer id) throws BrandNotFoundException {
        Long countById = brandRepository.countById(id);
        if (countById == null || countById == 0) {
            throw new BrandNotFoundException("Could not find any brand with ID " + id);
        }
        brandRepository.deleteById(id);
    }

}
