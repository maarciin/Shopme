package com.shopme.admin.brand;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class BrandRepositoryTests {

    @Autowired
    private BrandRepository brandRepository;

    @Test
    public void testCreateBrand1() {
        Category laptops = new Category(6);
        Brand acer = new Brand("Acer", "brand-logo.png");
        acer.getCategories().add(laptops);

        Brand savedBrand = brandRepository.save(acer);

        assertThat(savedBrand).isNotNull();
        assertThat(savedBrand.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateBrand2() {
        Category cellphones = new Category(4);
        Category tablets = new Category(7);

        Brand apple = new Brand("Apple", "brand-logo.png");
        apple.getCategories().add(cellphones);
        apple.getCategories().add(tablets);
        Brand savedBrand = brandRepository.save(apple);

        assertThat(savedBrand).isNotNull();
        assertThat(savedBrand.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateBrand3() {
        Category memory = new Category(29);
        Category internalHardDrive = new Category(24);

        Brand samsung = new Brand("Samsung", "brand-logo.png");
        samsung.getCategories().add(memory);
        samsung.getCategories().add(internalHardDrive);
        Brand savedBrand = brandRepository.save(samsung);

        assertThat(savedBrand).isNotNull();
        assertThat(savedBrand.getId()).isGreaterThan(0);
    }

    @Test
    public void testFindAll() {
        Iterable<Brand> brands = brandRepository.findAll();
        brands.forEach(System.out::println);

        assertThat(brands).isNotEmpty();
    }

    @Test
    public void testFindById() {
        Optional<Brand> brand = brandRepository.findById(1);
        brand.ifPresent(System.out::println);
        assertThat(brand).isPresent();
        assertThat(brand.get().getName()).isEqualTo("Acer");
    }

    @Test
    public void testUpdateName() {
        String newName = "Samsung Electronics";
        Optional<Brand> brand = brandRepository.findById(3);
        brand.ifPresent(b -> {
            b.setName(newName);
            Brand savedBrand = brandRepository.save(b);
            assertThat(savedBrand.getName()).isEqualTo(newName);
        });
    }

    @Test
    public void testDelete() {
        brandRepository.deleteById(3);

        Optional<Brand> brand = brandRepository.findById(3);
        assertThat(brand).isEmpty();
    }

    @Test
    public void testPagination(){
        int pageNumber = 0;
        int pageSize = 5;

        Sort sort = Sort.by("name").ascending();
        Pageable pageable =  PageRequest.of(pageNumber, 5, sort);

        Page<Brand> brands = brandRepository.findAll(pageable);
        brands.forEach(System.out::println);

        assertThat(brands.getSize()).isEqualTo(pageSize);
    }

}