package com.shopme.admin.brand;

import com.shopme.common.entity.Brand;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class BrandServiceTests {

    @Mock
    private BrandRepository repo;

    @InjectMocks
    private BrandService service;

    @Test
    public void testCheckUniqueInNewModeReturnFalse() {
        Integer id = null;
        String name = "Acer";
        Brand brand = new Brand(name);

        Mockito.when(repo.findByName(name)).thenReturn(Optional.of(brand));
        boolean isUnique = service.isBrandUnique(id, name);

        assertFalse(isUnique);
    }

    @Test
    public void testCheckUniqueNewModeReturnTrue() {
        Integer id = null;
        String name = "AMD";

        Mockito.when(repo.findByName(name)).thenReturn(Optional.empty());
        boolean isUnique = service.isBrandUnique(id, name);

        assertTrue(isUnique);

    }

    @Test
    public void testCheckUniqueInEditModeReturnFalse() {
        Integer id = 1;
        String name = "Acer";
        Brand brand = new Brand(id, name);

        Mockito.when(repo.findByName(name)).thenReturn(Optional.of(brand));
        boolean isUnique = service.isBrandUnique(2, name);

        assertFalse(isUnique);
    }

    @Test
    public void testCheckUniqueInEditModeReturnTrue() {
        Integer id = 1;
        String name = "Acer";
        Brand brand = new Brand(id, name);

        Mockito.when(repo.findByName(name)).thenReturn(Optional.of(brand));
        boolean isUnique = service.isBrandUnique(1, name);

        assertTrue(isUnique);
    }
}