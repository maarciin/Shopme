package com.shopme.category;

import com.shopme.common.entity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
