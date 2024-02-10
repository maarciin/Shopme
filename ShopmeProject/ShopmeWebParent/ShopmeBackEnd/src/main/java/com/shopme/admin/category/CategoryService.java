package com.shopme.admin.category;

import com.shopme.common.entity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> listAll() {
        List<Category> rootCategories = categoryRepository.findRootCategories();
        return listHierarchicalCategories(rootCategories);
    }

    private List<Category> listHierarchicalCategories(List<Category> rootCategories) {
        List<Category> hierarchicalCategories = new ArrayList<>();

        for (Category rootCategory : rootCategories) {
            hierarchicalCategories.add(Category.copyFull(rootCategory));

            for (Category subCategory : rootCategory.getChildren()) {
                String name = "--" + subCategory.getName();
                hierarchicalCategories.add(Category.copyFull(subCategory, name));

                listSubHierarchicalCategories(hierarchicalCategories, subCategory, 1);
            }
        }

        return hierarchicalCategories;
    }

    private void listSubHierarchicalCategories(List<Category> hierarchicalCategories, Category parent, int subLevel) {
        for (Category subCategory : parent.getChildren()) {
            String name = "";
            for (int i = 0; i <= subLevel; i++) {
                name += "--";
            }
            name += subCategory.getName();

            hierarchicalCategories.add(Category.copyFull(subCategory, name));

            listSubHierarchicalCategories(hierarchicalCategories, subCategory, subLevel);
        }
    }

    public List<Category> listCategoriesUsedInForm() {
        List<Category> categoriesUsedInForm = new ArrayList<>();
        Iterable<Category> categoriesInDB = categoryRepository.findAll();

        for (Category category : categoriesInDB) {
            if (category.getParent() == null) {
                categoriesUsedInForm.add(Category.copyIdAndName(category));

                for (Category subCategory : category.getChildren()) {
                    String subCategoryName = "--" + subCategory.getName();
                    categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), subCategoryName));
                    listSubCategoriesUsedInForm(categoriesUsedInForm, subCategory, 1);
                }
            }
        }
        return categoriesUsedInForm;
    }

    private void listSubCategoriesUsedInForm(List<Category> categoriesUsedInForm, Category parent, int subLevel) {
        for (Category subCategory : parent.getChildren()) {
            String name = "";
            for (int i = 0; i <= subLevel; i++) {
                name += "--";
            }
            name += subCategory.getName();
            categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));

            listSubCategoriesUsedInForm(categoriesUsedInForm, subCategory, subLevel);
        }
    }

    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    public Category getById(Integer id) throws CategoryNotFoundException {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Could not find any category with ID " + id));
    }

    public String checkUnique(Integer id, String name, String alias) {
        boolean isCreateMode = (id == null || id == 0);

        Optional<Category> categoryByName = categoryRepository.findByName(name);
        Optional<Category> categoryByAlias = categoryRepository.findByAlias(alias);

        if (isCreateMode) {
            if (categoryByName.isPresent()) {
                return "DuplicateName";
            } else {
                if (categoryByAlias.isPresent()) {
                    return "DuplicateAlias";
                }
            }
        } else {
            if (categoryByName.isPresent() && categoryByName.get().getId() != id) {
                return "DuplicateName";
            }
            if (categoryByAlias.isPresent() && categoryByAlias.get().getId() != id) {
                return "DuplicateAlias";
            }
        }
        return "OK";
    }
}
