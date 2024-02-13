package com.shopme.admin.category;

import com.shopme.common.entity.Category;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    public static final int ROOT_CATEGORIES_PER_PAGE = 4;

    private final CategoryRepository categoryRepository;

    public List<Category> listByPage(CategoryPageInfo pageInfo, int pageNum, String sortDir, String keyword) {
        Sort sort = Sort.by("name");

        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, ROOT_CATEGORIES_PER_PAGE, sort);

        Page<Category> pageCategories;
        if (keyword != null && !keyword.isEmpty()) {
            pageCategories = categoryRepository.search(keyword, pageable);
        } else {
            pageCategories = categoryRepository.findRootCategories(pageable);
        }

        List<Category> rootCategories = pageCategories.getContent();

        pageInfo.setTotalElements(pageCategories.getTotalElements());
        pageInfo.setTotalPages(pageCategories.getTotalPages());

        if (keyword != null && !keyword.isEmpty()) {
            List<Category> searchResult = pageCategories.getContent();
            for (Category category : searchResult) {
                category.setHasChildren(!category.getChildren().isEmpty());
            }
            return searchResult;
        } else {
            return listHierarchicalCategories(rootCategories, sortDir);
        }
    }

    private List<Category> listHierarchicalCategories(List<Category> rootCategories, String sortDir) {
        List<Category> hierarchicalCategories = new ArrayList<>();

        for (Category rootCategory : rootCategories) {
            hierarchicalCategories.add(Category.copyFull(rootCategory));

            Set<Category> children = sortSubCategories(rootCategory.getChildren(), sortDir);

            for (Category subCategory : children) {
                String name = "--" + subCategory.getName();
                hierarchicalCategories.add(Category.copyFull(subCategory, name));

                listSubHierarchicalCategories(hierarchicalCategories, subCategory, 1, sortDir);
            }
        }

        return hierarchicalCategories;
    }

    private void listSubHierarchicalCategories(List<Category> hierarchicalCategories, Category parent,
                                               int subLevel, String sortDir) {
        Set<Category> children = sortSubCategories(parent.getChildren(), sortDir);
        for (Category subCategory : children) {
            String name = "";
            for (int i = 0; i <= subLevel; i++) {
                name += "--";
            }
            name += subCategory.getName();

            hierarchicalCategories.add(Category.copyFull(subCategory, name));

            listSubHierarchicalCategories(hierarchicalCategories, subCategory, subLevel, sortDir);
        }
    }

    public List<Category> listCategoriesUsedInForm() {
        List<Category> categoriesUsedInForm = new ArrayList<>();
        Iterable<Category> categoriesInDB = categoryRepository.findRootCategories(Sort.by("name").ascending());

        for (Category category : categoriesInDB) {
            if (category.getParent() == null) {
                categoriesUsedInForm.add(Category.copyIdAndName(category));

                Set<Category> children = sortSubCategories(category.getChildren());

                for (Category subCategory : children) {
                    String subCategoryName = "--" + subCategory.getName();
                    categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), subCategoryName));
                    listSubCategoriesUsedInForm(categoriesUsedInForm, subCategory, 1);
                }
            }
        }
        return categoriesUsedInForm;
    }

    private void listSubCategoriesUsedInForm(List<Category> categoriesUsedInForm, Category parent, int subLevel) {
        Set<Category> children = sortSubCategories(parent.getChildren());
        for (Category subCategory : children) {
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

    private SortedSet<Category> sortSubCategories(Set<Category> children) {
        return sortSubCategories(children, "asc");
    }

    private SortedSet<Category> sortSubCategories(Set<Category> children, String sortDir) {
        SortedSet<Category> sortedChildren = new TreeSet<>((cat1, cat2) -> {
            if (sortDir.equals("asc")) {
                return cat1.getName().compareTo(cat2.getName());
            } else {
                return cat2.getName().compareTo(cat1.getName());
            }
        });
        sortedChildren.addAll(children);
        return sortedChildren;
    }

    public void updateCategoryEnabledStatus(Integer id, boolean status) {
        categoryRepository.updateEnabledStatus(id, status);
    }

    public void deleteCategory(Integer id) throws CategoryNotFoundException {
        Long countById = categoryRepository.countById(id);
        if (countById == null || countById == 0) {
            throw new CategoryNotFoundException("Could not find any category with ID " + id);
        }
        categoryRepository.deleteById(id);
    }
}
