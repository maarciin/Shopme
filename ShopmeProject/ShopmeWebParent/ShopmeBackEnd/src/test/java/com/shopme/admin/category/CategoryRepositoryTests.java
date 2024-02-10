package com.shopme.admin.category;

import com.shopme.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class CategoryRepositoryTests {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void testCreateRootCategory() {
        Category category = new Category("Electronics");
        Category savedCategory = categoryRepository.save(category);

        assertThat(savedCategory.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateSubCategory() {
        Category parent = new Category(7);
        Category ihpone = new Category("Iphone", parent);

        Category savedCategory = categoryRepository.save(ihpone);

        assertThat(savedCategory.getId()).isGreaterThan(0);
    }

    @Test
    public void testGetCategory() {
        Optional<Category> optionalCategory = categoryRepository.findById(1);

        if (optionalCategory.isPresent()) {
            Category category = optionalCategory.get();
            System.out.println(category.getName());

            for (Category subcategory : category.getChildren()) {
                System.out.println("--" + subcategory.getName());
            }
        }

        assertThat(optionalCategory).isPresent();
    }

    @Test
    public void testPrintHierarchicalCategories() {
        Iterable<Category> categories = categoryRepository.findAll();

        for (Category category : categories) {
            if(category.getParent() == null) {
                System.out.println(category.getName());

                Set<Category> subCategories = category.getChildren();
                for (Category subCategory : subCategories) {
                    System.out.println("--" + subCategory.getName());
                    printChildren(subCategory, 1);
                }
            }

        }
    }

    private void printChildren(Category parent, int subLevel) {
        for (Category subCategory : parent.getChildren()) {
            for (int i = 0; i <= subLevel; i++) {
                System.out.print("--");
            }
            System.out.println(subCategory.getName());
        }
    }

    @Test
    public void testListRootCategories() {
        List<Category> rootCategories = categoryRepository.findRootCategories();
        rootCategories.forEach(category -> System.out.println(category.getName()));
    }
    
    @Test
    public void testFindByName() {
        String name = "Computers";
        Optional<Category> category = categoryRepository.findByName(name);

        assertTrue(category.isPresent());
        category.ifPresent(value -> assertThat(value.getName()).isEqualTo(name));
    }

    @Test
    public void testFindByAlias() {
        String alias = "Electronics";
        Optional<Category> category = categoryRepository.findByAlias(alias);

        assertTrue(category.isPresent());
        category.ifPresent(value -> assertThat(value.getName()).isEqualTo(alias));
    }
}