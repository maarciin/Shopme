package com.shopme.category;

import com.shopme.common.entity.Category;
import com.shopme.common.exception.CategoryNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> listNoChildrenCategories() {
        List<Category> listEnabledCategories = categoryRepository.findAllEnabled();

        return listEnabledCategories.stream()
                .filter(category -> category.getChildren() == null || category.getChildren().isEmpty())
                .toList();
    }

    public Category getCategory(String alias) throws CategoryNotFoundException {
        return categoryRepository.findByAliasEnabled(alias)
                .orElseThrow(() -> new CategoryNotFoundException("Could not find any category with alias " + alias));
    }

    public List<Category> getCategoryParents(Category childCategory) {
        List<Category> listParents = new ArrayList<>();

        Category parent = childCategory.getParent();
        while (parent != null) {
            listParents.add(0, parent);
            parent = parent.getParent();
        }

        listParents.add(childCategory);

        return listParents;
    }
}
