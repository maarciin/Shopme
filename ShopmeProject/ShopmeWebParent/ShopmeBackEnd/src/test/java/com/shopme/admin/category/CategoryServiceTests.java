package com.shopme.admin.category;

import com.shopme.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CategoryServiceTests {

    @Mock
    private CategoryRepository repo;

    @InjectMocks
    private CategoryService service;

    @Test
    public void testCheckUniqueInNewModeReturnDuplicateName() {
        Integer id = null;
        String name = "Computers";
        String alias = "abc";

        Category category = new Category(id, name, alias);

        Mockito.when(repo.findByName(name)).thenReturn(Optional.of(category));
        Mockito.when(repo.findByAlias(alias)).thenReturn(Optional.empty());

        String result = service.checkUnique(id, name, alias);

        assertThat(result).isEqualTo("DuplicateName");
    }

    @Test
    public void testCheckUniqueInNewModeReturnDuplicateAlias() {
        Integer id = null;
        String name = "NameABC";
        String alias = "Computers";

        Category category = new Category(id, name, alias);

        Mockito.when(repo.findByName(name)).thenReturn(Optional.empty());
        Mockito.when(repo.findByAlias(alias)).thenReturn(Optional.of(category));

        String result = service.checkUnique(id, name, alias);

        assertThat(result).isEqualTo("DuplicateAlias");
    }

    @Test
    public void testCheckUniqueInNewModeReturnOK() {
        Integer id = null;
        String name = "NameABC";
        String alias = "clothes";

        Mockito.when(repo.findByName(name)).thenReturn(Optional.empty());
        Mockito.when(repo.findByAlias(alias)).thenReturn(Optional.empty());

        String result = service.checkUnique(id, name, alias);

        assertThat(result).isEqualTo("OK");
    }

    @Test
    public void testCheckUniqueInEditModeReturnDuplicateName() {
        Integer id = 1;
        String name = "Computers";
        String alias = "abc";

        Category category = new Category(2, name, alias);

        Mockito.when(repo.findByName(name)).thenReturn(Optional.of(category));
        Mockito.when(repo.findByAlias(alias)).thenReturn(Optional.empty());

        String result = service.checkUnique(id, name, alias);

        assertThat(result).isEqualTo("DuplicateName");
    }

    @Test
    public void testCheckUniqueInEditModeReturnDuplicateAlias() {
        Integer id = 1;
        String name = "NameABC";
        String alias = "Computers";

        Category category = new Category(2, name, alias);

        Mockito.when(repo.findByName(name)).thenReturn(Optional.empty());
        Mockito.when(repo.findByAlias(alias)).thenReturn(Optional.of(category));

        String result = service.checkUnique(id, name, alias);

        assertThat(result).isEqualTo("DuplicateAlias");
    }

    @Test
    public void testCheckUniqueInEditModeReturnOK() {
        Integer id = 1;
        String name = "NameABC";
        String alias = "clothes";

        Category category = new Category(id, name, alias);

        Mockito.when(repo.findByName(name)).thenReturn(Optional.empty());
        Mockito.when(repo.findByAlias(alias)).thenReturn(Optional.of(category));

        String result = service.checkUnique(id, name, alias);

        assertThat(result).isEqualTo("OK");
    }
}