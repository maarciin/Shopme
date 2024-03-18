package com.shopme.admin.brand;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.common.entity.Brand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BrandService {

    public static final int BRANDS_PER_PAGE = 10;

    private final BrandRepository brandRepository;

    public List<Brand> listAll() {
        return brandRepository.findAll();
    }

    public void listByPage(int pageNum, PagingAndSortingHelper helper) {
        helper.listEntities(pageNum, BRANDS_PER_PAGE, brandRepository);
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

    public boolean isBrandUnique(Integer id, String name) {
        Optional<Brand> brand = brandRepository.findByName(name);
        if (brand.isEmpty()) return true;

        if (id == null) {
            return false;
        } else {
            return Objects.equals(brand.get().getId(), id);
        }
    }

}
