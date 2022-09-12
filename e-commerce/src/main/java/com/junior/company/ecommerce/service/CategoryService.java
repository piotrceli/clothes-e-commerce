package com.junior.company.ecommerce.service;

import com.junior.company.ecommerce.dto.CategoryRequest;
import com.junior.company.ecommerce.dto.CategoryResponse;
import com.junior.company.ecommerce.dto.ProductResponse;

import java.util.List;
import java.util.Set;

public interface CategoryService {

    Set<CategoryResponse> findCategories();

    CategoryResponse findCategoryByName(String categoryName);

    CategoryResponse addCategory(CategoryRequest categoryRequest);

    CategoryResponse updateCategory(CategoryRequest categoryRequest);

    boolean deleteCategoryById(Long categoryId);

    List<ProductResponse> viewProducts(String categoryName);
}
