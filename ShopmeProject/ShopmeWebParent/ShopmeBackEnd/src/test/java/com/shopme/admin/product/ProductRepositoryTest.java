package com.shopme.admin.product;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.product.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateProduct() {
        Brand brand = entityManager.find(Brand.class, 37);
        Category category = entityManager.find(Category.class, 5);

        Product product = new Product();
        product.setName("Acer Aspire Desktop");
        product.setAlias("acer_aspire_desktop");
        product.setShortDescription("Short description for Acer Aspire");
        product.setFullDescription("Full description for Acer Aspire");

        product.setBrand(brand);
        product.setCategory(category);

        product.setPrice(678);
        product.setCost(600);
        product.setEnabled(true);
        product.setInStock(true);

        product.setCreatedTime(new Date());
        product.setUpdatedTime(new Date());

        Product savedProduct = productRepository.save(product);

        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateProduct2() {
        Brand brand = entityManager.find(Brand.class, 38);
        Category category = entityManager.find(Category.class, 6);

        Product product = new Product();
        product.setName("Dell Inspiron 3000");
        product.setAlias("dell_inspriton_3000");
        product.setShortDescription("Short description for Dell Inspiron 3000");
        product.setFullDescription("Full description for Dell Inspiron 3000");

        product.setBrand(brand);
        product.setCategory(category);

        product.setPrice(456);
        product.setCost(400);
        product.setEnabled(true);
        product.setInStock(true);

        product.setCreatedTime(new Date());
        product.setUpdatedTime(new Date());

        Product savedProduct = productRepository.save(product);

        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isGreaterThan(0);
    }

    @Test
    public void testListAllProducts() {
        Iterable<Product> products = productRepository.findAll();
        products.forEach(System.out::println);
    }

    @Test
    public void testGetProduct() {
        Integer id = 2;
        Optional<Product> product = productRepository.findById(id);
        assertThat(product).isNotEmpty();
    }

    @Test
    public void testUpdateProduct() {
        Integer id = 1;
        Optional<Product> product = productRepository.findById(1);
        product.ifPresent(pr -> {
            pr.setPrice(499);
            productRepository.save(pr);
        });

        Product updatedProduct = entityManager.find(Product.class, id);
        assertThat(updatedProduct.getPrice()).isEqualTo(499);
    }

    @Test
    public void testDeleteProduct() {
        Integer id = 3;
        productRepository.deleteById(id);

        Optional<Product> product = productRepository.findById(3);
        assertThat(product).isEmpty();
    }

    @Test
    public void testSaveProductWithImages() {
        Integer id = 1;
        Product product = productRepository.findById(id).get();

        product.setMainImage("main image.jpg");
        product.addExtraImage("extra image 1.jpg");
        product.addExtraImage("extra image 2.jpg");
        product.addExtraImage("extra image 3.jpg");
        Product savedProduct = productRepository.save(product);

        assertThat(savedProduct.getImages().size()).isEqualTo(3);
    }

    @Test
    public void testSaveProductWithDetails() {
        Integer productId = 1;
        Product product = productRepository.findById(productId).get();

        product.addDetail("Device Memory", "128GB");
        product.addDetail("CPU Model", "MediaTek");
        product.addDetail("OS", "Android 10");

        Product savedProduct = productRepository.save(product);
        assertThat(savedProduct.getDetails()).isNotEmpty();
    }

}