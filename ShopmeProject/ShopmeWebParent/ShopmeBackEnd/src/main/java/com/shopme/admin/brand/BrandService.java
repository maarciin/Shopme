package com.shopme.admin.brand;

import com.shopme.common.entity.Brand;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BrandService {

    public static final int BRANDS_PER_PAGE = 10;

    private final BrandRepository brandRepository;

    public List<Brand> findAll() {
        return (List<Brand>) brandRepository.findAll();
    }

    public Page<Brand> listByPage(int pageNum, String sortField, String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, BRANDS_PER_PAGE, sort);

        if (keyword != null) {
            return brandRepository.search(keyword, pageable);
        }

        return brandRepository.findAll(pageable);
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
